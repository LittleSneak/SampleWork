#include <vector>
#include <iostream>

#define num_attributes 100
#define byte_per_attribute 10

typedef const char* V; 
typedef std::vector<V> Record; //records as a tuple of values.

/**
 * Compute the number of bytes required to serialize record
 */
int fixed_len_sizeof(Record *record);

/**
 * Serialize the record to a byte array to be stored in buf.
 */
void fixed_len_write(Record *record, void *buf);

/**
 * Deserializes `size` bytes from the buffer, `buf`, and
 * stores the record in `record`.
 */
void fixed_len_read(void *buf, int size, Record *record);


// PART 3 ----------------------------------------------

/** 
 * Use slotted directory based page layout to store fixed length records.
 */
typedef struct {
    void *data;
    int page_size;
    int slot_size;
	int directory_size; // the size of the bytemap
	bool isDir;
} Page;


/**
 * Initializes a page using the given slot size
 */
void init_fixed_len_page(Page *page, int page_size, int slot_size);
 
/**
 * Calculates the maximal number of records that fit in a page
 */
int fixed_len_page_capacity(Page *page);
 
/**
 * Calculate the free space (number of free slots) in the page
 */
int fixed_len_page_freeslots(Page *page);
 
/**
 * Add a record to the page
 * Returns:
 *   record slot offset if successful,
 *   -1 if unsuccessful (page full)
 */
int add_fixed_len_page(Page *page, Record *r);
 
/**
 * Write a record into a given slot.
 */
void write_fixed_len_page(Page *page, int slot, Record *r);
 
/**
 * Read a record from the page from a given slot.
 */
void read_fixed_len_page(Page *page, int slot, Record *r);

/**
 * Returns the size of the bytemap of the page
 */
int bytemap_size(Page *page);

// END PART 3-------------------------------------------


// PART 4 ---------------------------------------------

// Each page is to store a series of records.

//At the very head of the heapfile
typedef struct dir_head{
	int num_dirs;
	int num_pages;
	int page_size;
} DirHeader;

//Heap file structure is as follows:
//DirHeader followed immediately by
//Directory - Page1 - Page2 - Page3 - Page4- Page5 - Directory - Page1...
//Pages only contain the byte map followed by the records
typedef struct {
    FILE *file_ptr;
    int page_size;
	struct dir_head *dirhdr;
} Heapfile;

// structures to abstract page ID and record ID.
typedef int PageID;
 
typedef struct {
    int page_id;
    int slot;
} RecordID;

typedef struct{
	int offset;
	int free_slot;
} DirEntry;

/**
 * Initalize a heapfile to use the file and page size given.
 */
void init_heapfile(Heapfile *heapfile, int page_size, FILE *file);

/**
 * Allocate another page in the heapfile.  This grows the file by a page.
 */
PageID alloc_page(Heapfile *heapfile);

/**
 * Read a page into memory
 */
void read_page(Heapfile *heapfile, PageID pid, Page *page);

/**
 * Write a page from memory to disk
 */
void write_page(Page *page, Heapfile *heapfile, PageID pid);

/**
The central functionality of a heap file is enumeration of records.
Implement the record iterator class.
*/
class RecordIterator {
	Heapfile *heapfile;
	int curr_pid;
    int curr_slot;
	Page *curr_page;
	Record *curr_record;
	bool has_next;
	
public:
    RecordIterator(Heapfile *heapfile);
    Record *next();
    bool hasNext();
};

void writePage(Page *p);
