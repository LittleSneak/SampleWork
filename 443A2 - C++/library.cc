#include <cstring>
#include "library.h"
#include <vector>
#include <stdio.h>
#include <cstdlib>

/**
 * Compute the number of bytes required to serialize record
 */
int fixed_len_sizeof(Record *record) {
	// number of tuples * number of attributes * size of attribute
    // int num_bytes = 0;
    // for (std::vector<V>::iterator it = record->begin(); it != record->end(); ++it) {
    //     num_bytes += std::strlen(*it);
    // }
    // return num_bytes;
    return num_attributes * byte_per_attribute;
}

/**
 * Serialize the record to a byte array to be stored in buf.
 */
void fixed_len_write(Record *record, void *buf) {

    for (size_t i = 0; i < record->size(); i++){
    	memcpy(((char*)buf + i * byte_per_attribute), record->at(i), byte_per_attribute);
    }

}

/**
 * Deserializes `size` bytes from the buffer, `buf`, and
 * stores the record in `record`.
 */
void fixed_len_read(void *buf, int size, Record *record) {

    int num_records = size / byte_per_attribute;

    for (int i = 0; i < num_records; i++) {
        V attribute = (char *) buf + i * byte_per_attribute;
        record->push_back(attribute); // store in record
    }
}
// PART 3 PAGE LAYOUT ------------------------------------------------------

/**
 * Initializes a page using the given slot size
 */
void init_fixed_len_page(Page *page, int page_size, int slot_size){
	// initialization of page
	page->data = (void *) malloc(page_size);
	memset(page->data, 0, page_size);
	page->page_size = page_size;
    page->slot_size = slot_size;
}

/**
 * Calculates the maximal number of records that fit in a page.
 * Available pagesize is the location of the bitmap
 */
int fixed_len_page_capacity(Page *page){
	return bytemap_size(page);
}

/**
 * Calculate the free space (number of free slots) in the page
 */
int fixed_len_page_freeslots(Page *page){
	char *data = (char *) page->data;
	int count = 0;
	for (int x = 0; x < bytemap_size(page); x++){
		if(data[x] == 0){
			count++;
		}
	}
	return count;
}

/**
 * Add a record to the page
 * Returns:
 *   record slot offset if successful,
 *   -1 if unsuccessful (page full)
 */
int add_fixed_len_page(Page *page, Record *r){
	char *data = (char *) page->data;
	for (int x = 0; x < bytemap_size(page); x++){
		//Found empty slot, update bytemap and write
		if(data[x] == 0){
			memset(((char *)page->data) + x, 1, 1);
			write_fixed_len_page(page, x, r);
		    return x;
		}
	}
	return -1;
}

/**
 * Write a record into a given slot.
 */
void write_fixed_len_page(Page *page, int slot, Record *r){
	fixed_len_write(r, ((char *) page->data) + bytemap_size(page) + slot * 1000);
	page->slot_size++;
}

/**
 * Read a record from the page from a given slot.
 */
void read_fixed_len_page(Page *page, int slot, Record *r){
	// slot outside the maximum slot a page can hold
	if (slot > page->directory_size){
		return;
	}
	char* slot_ptr = (char*)page->data + (page->slot_size * slot);
    fixed_len_read(slot_ptr, page->slot_size, r);
}

/**
 * Get size of the bytemap
 */
int bytemap_size(Page *page){
	int size = page->page_size / page->slot_size;
	int sizeOfPages = page->slot_size * size; //The bytes used by proposed number of pages

	//Decrease number of slots until there's enough room for a bytemap
	while(size > page->page_size - sizeOfPages){
		size = size - 1;
		sizeOfPages = page->slot_size * size;
	}
	return size;
}

// END PART 3 PAGE LAYOUT --------------------------------------------------

// PART 4 HEAPFILE --------------------------------------------------
/**
 * Initalize a heapfile to use the file and page size given.
 */
void init_heapfile(Heapfile *heapfile, int page_size, FILE *file){
	heapfile->file_ptr = file;
	heapfile->page_size = page_size;

	//Initialize the directory header and put it in the heapfile struct
	//Main functions are responsible for reading proper header from file
	DirHeader *dirhdr = (DirHeader *) malloc(sizeof(DirHeader));
	dirhdr->num_pages = 0;
	dirhdr->num_dirs = 0;
	dirhdr->page_size = page_size;
	heapfile->dirhdr = dirhdr;
}

/**
 * Allocate another page in the heapfile.  This grows the file by a page.
 */
PageID alloc_page(Heapfile *heapfile){

	//Init new page
	Page *dir = (Page *) malloc(sizeof(Page));;
	init_fixed_len_page(dir, heapfile->page_size, 1000);

	//New directory entry
	DirEntry *dentry = (DirEntry *) malloc(sizeof(DirEntry));

	int max_pages = heapfile->page_size / sizeof(DirEntry);
	//The size of a directory page and all the pages it can hold
	int fullDirSize = (heapfile->page_size + 1) * max_pages;

	//The index of where the next page should go
	int pageIndex = heapfile->dirhdr->num_pages % max_pages;
	heapfile->dirhdr->num_pages++;
	//Check if a new directory is needed
	if(heapfile->dirhdr->num_pages == 0 || pageIndex == 0){
		//Init a new directory and put in page info
		dir->isDir = true;
		//Update dentry files
		dentry->offset = 0;
		dentry->free_slot = heapfile->page_size / 1000;
		memcpy(dir->data, dentry, sizeof(DirEntry));
		//Change values in dirhdr
		heapfile->dirhdr->num_dirs++;
	}
	//There's already a dir with an unallocated page
	else{
		//Go to last directory with an unallocated page
		fseek(heapfile->file_ptr, sizeof(DirHeader) + (fullDirSize * (heapfile->dirhdr->num_dirs - 1)), SEEK_SET);
		fread(dir->data, heapfile->page_size, 1, heapfile->file_ptr);

		dentry->offset = pageIndex * heapfile->page_size;
		dentry->free_slot = heapfile->page_size / 1000;
		//Add directory entry to directory page
		memcpy(((char *)dir->data) + pageIndex * sizeof(DirEntry), dentry, sizeof(DirEntry));
	}
	//Write new or updated directory page to disk
	fseek(heapfile->file_ptr, sizeof(DirHeader) + (fullDirSize * (heapfile->dirhdr->num_dirs - 1)), SEEK_SET);
	fwrite(dir->data, heapfile->page_size, 1, heapfile->file_ptr);

	return heapfile->dirhdr->num_pages - 1;
}

/**
 * Read a page into memory
 */
void read_page(Heapfile *heapfile, PageID pid, Page *page){

	int max_pages = heapfile->page_size / sizeof(DirEntry);

	//Calculate which directory it is in and which page of the dir
	int pageIndex = (pid % max_pages) + 1;
	int dirIndex = pid / max_pages;

	//The size of a directory page and all the pages it can hold
	int fullDirSize = (heapfile->page_size + 1) * max_pages;

	//Go to where the page is
	fseek(heapfile->file_ptr, sizeof(DirHeader) + (fullDirSize * dirIndex) +
	    (pageIndex * heapfile->page_size), SEEK_SET);
	fread(page->data, heapfile->page_size, 1, heapfile->file_ptr);
}

/**
 * Write a page from memory to disk and does nothing else
 */
void write_page(Page *page, Heapfile *heapfile, PageID pid){

	int max_pages = heapfile->page_size / sizeof(DirEntry);

	//Calculate which directory it is in and which page of the dir
	int pageIndex = (pid % max_pages) + 1;
	int dirIndex = pid / max_pages;

	//The size of a directory page and all the pages it can hold
	int fullDirSize = (heapfile->page_size + 1) * max_pages;

	//Go to where the page is
	fseek(heapfile->file_ptr, sizeof(DirHeader) + (fullDirSize * dirIndex) +
	    (pageIndex * heapfile->page_size), SEEK_SET);
	fwrite(page->data, page->page_size, 1, heapfile->file_ptr);
}

/**
 * RecordIterator implementation
 */

/**
 * Initialiaze the heapfile as an iterator of records
 */
RecordIterator::RecordIterator(Heapfile *heapfile){
	this->heapfile = heapfile;
	this->curr_pid = 0;
	this->curr_slot = -1;
	//Get first page
	this->curr_page = (Page *)malloc(sizeof(Page));
	init_fixed_len_page(this->curr_page, heapfile->page_size, 1000);
	
	read_page(heapfile, this->curr_pid, this->curr_page);
	//Get first record
	this->curr_record = NULL;
	this->has_next = true;
	this->next();
}

/**
 * Return the next record in the heapfile if record exists
 * If there is no next record, return null
 */
Record *RecordIterator::next() {
	Record *holder = this->curr_record;
	char *data;
	
	//Grab next record
	Record *r = (Record *) malloc(sizeof(Record));
	//Not within page or empty slot
	this->curr_slot++;
	while(this->curr_slot >= (bytemap_size(this->curr_page)) || ((char *)this->curr_page->data)[this->curr_slot] == 0){
		//We reached the end of the page get a new one
		if(this->curr_slot >= (bytemap_size(this->curr_page))){
			this->curr_pid++;
			//No pages left
			if(this->curr_pid > this->heapfile->dirhdr->num_pages - 1){
				this->curr_record = NULL;
				this->has_next = false;
				return holder;
			}
			this->curr_slot = 0;
			read_page(this->heapfile, this->curr_pid, this->curr_page);
		}
		//Look for nonempty slot in this page
		while(this->curr_slot < (bytemap_size(this->curr_page)) && ((char *)this->curr_page->data)[this->curr_slot] == 0){
			this->curr_slot++;
		}
		//Loop back up if not found
	}
	
	//Found a new record so store it
	if(this->curr_slot >= (bytemap_size(this->curr_page)) || ((char *)this->curr_page->data)[this->curr_slot] == 1){
		data = ((char *)this->curr_page->data) + bytemap_size(this->curr_page) + 1000 * this->curr_slot;
		char *attribute;
		char *newAttribute;
		//Go through every attribute in this and add it to record
		for(int x = 0; x < 100; x++){
			newAttribute = (char *) malloc(11);
			memcpy(newAttribute, data + x * 10, 10);
			newAttribute[10] = '\0';
			(*r).push_back(newAttribute);
		}
		this->curr_record = r;
	}
	return holder;
}

/**
 * Return 1 if Iterator has next record, -1 if there is no next record.
 */

 bool RecordIterator::hasNext() {
	return this->has_next;
 }
 
 //For debugging
 void writePage(Page *p){
	int count1;
	int count2;
	int count3;
	 
	char *word;
	 
	 //Go through every record in the page
	word = ((char *) p->data) + bytemap_size(p);
	for(count1 = 0; count1 < p->page_size - bytemap_size(p); count1++){
		std::cout << word[count1];
		if(count1 % 10 == 0 && count1 != 0){
			std::cout << ",";
		}
		if(count1 % 1000 == 0 && count1 != 0){
			std::cout << "\n";
		}
	}
 }
