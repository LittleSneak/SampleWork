#include <fstream>
#include <stdio.h>
#include <string>
#include <iostream>
#include <sys/timeb.h>
#include <cstring>
#include "library.h"
#include <stdlib.h>

int main(int argc, char *argv[]){
	if(argc < 5){
		std::cout<<"Arguments: <heapfile> <slot> <pid> <page_size>\n";
		return -1;
	}
	
	//Init heapfile
	FILE *file = fopen(argv[1], "r+");
	Heapfile *hf = (Heapfile *) malloc(sizeof(Heapfile));
	init_heapfile(hf, atoi(argv[4]), file);
	
	//Get the directory header
	fseek(file, 0, SEEK_SET);
	DirHeader *dirhdr = (DirHeader *) malloc(sizeof(DirHeader));
	fread(dirhdr, sizeof(DirHeader), 1, hf->file_ptr);
	
	//Make sure the correct page size is given
	if(dirhdr->page_size != atoi(argv[4])){
		std::cout<<"Page size mismatch\n";
		return -1;
	}
	
	int pid = atoi(argv[3]);
	int slot = atoi(argv[2]);
	
	//Get page with record
	Page *page = (Page *) malloc(sizeof(Page));
	init_fixed_len_page(page, atoi(argv[4]), 1000);
	read_page(hf, pid, page);
	//Delete the record
	memset(((char *)page->data) + slot, 0, 1);
	
	//Write change
	write_page(page, hf, pid);
	
	//Look for the directory with this page
	int max_pages = hf->page_size / sizeof(DirEntry);
	//Calculate which directory it is in and which page of the dir
	int pageIndex = pid % max_pages;
	int dirIndex = pid / max_pages;
	int fullDirSize = (hf->page_size + 1) * max_pages;
	fseek(hf->file_ptr, sizeof(DirHeader) + (fullDirSize * dirIndex), SEEK_SET);
	Page *dir = (Page *) malloc(sizeof(Page));
	init_fixed_len_page(dir, hf->page_size, 1000);
	fread(dir->data, hf->page_size, 1, hf->file_ptr);
	
	//Update page info in directory
	DirEntry *dentry = (DirEntry *) (((char *)dir->data) + pageIndex * sizeof(DirEntry));
	dentry->free_slot = dentry->free_slot - 1;
	//Write directory change to file
	fseek(hf->file_ptr, sizeof(DirHeader) + (fullDirSize * dirIndex), SEEK_SET);
	fwrite(dir->data, hf->page_size, 1, hf->file_ptr);
	fclose(file);
}