#include <fstream>
#include <stdio.h>
#include <string>
#include <iostream>
#include <sys/timeb.h>
#include <cstring>
#include "library.h"
#include <stdlib.h>

//Updates the attribute at the given page id and slot
//The first page has PID 0 and the first slot is at 0
int main(int argc, char *argv[]){
	if(argc < 7){
		std::cout<<"Arguments: <heapfile> <pid> <slot> <attribute_id> <new_value> <page_size>\n";
		return -1;
	}
	
	//Init heapfile
	FILE *file = fopen(argv[1], "r+");
	Heapfile *hf = (Heapfile *) malloc(sizeof(Heapfile));
	init_heapfile(hf, atoi(argv[6]), file);
	
	//Get the directory header
	fseek(file, 0, SEEK_SET);
	DirHeader *dirhdr = (DirHeader *) malloc(sizeof(DirHeader));
	fread(dirhdr, sizeof(DirHeader), 1, hf->file_ptr);
	
	//Make sure the correct page size is given
	if(dirhdr->page_size != atoi(argv[6])){
		fclose(file);
		std::cout<<"Page size mismatch"<<std::endl;
		return -1;
	}
	
	//Get command line arguments
	int pid = atoi(argv[2]);
	int slot = atoi(argv[3]);
	int attribute_id = atoi(argv[4]);
	const char *newVal = argv[5];
	
	//Check if PID is valid
	if(dirhdr->num_pages < pid){
		fclose(file);
		std::cout<<"Page does not exist"<<std::endl;
		return -1;
	}
	
	
	Page *page = (Page *) malloc(sizeof(Page));
	init_fixed_len_page(page, atoi(argv[6]), 1000);
	
	//Check if slot is valid
	if(slot > bytemap_size(page)){
		fclose(file);
		std::cout<<"Invalid slot";
		return -1;
	}
	//Read page with specified pid
	read_page(hf, pid, page);
	
	memcpy(((char *) page->data) + bytemap_size(page) + slot * 1000 + atoi(argv[4]) * 10, newVal, 10);
	write_page(page, hf, pid);
	
	std::cout<<"UPDATED"<<std::endl;
	fclose(file);
	return 0;
}