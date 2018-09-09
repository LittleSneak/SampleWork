#include <fstream>
#include <stdio.h>
#include <string>
#include <iostream>
#include <sys/timeb.h>
#include <cstring>
#include "library.h"
#include <stdlib.h>

//Insert records from a csv file into an existing heapfile
//Will return with an error if the page_size given does not
//match heapfile's page_size
int main(int argc, char *argv[]){
	if(argc < 4){
		std::cout<<"Arguments: <heapfile> <csv_file> <page_size>";
		return -1;
	}
	//Initialize heapfile
	FILE *file = fopen(argv[1], "r+");
	Heapfile *hf = (Heapfile *) malloc(sizeof(Heapfile));
	init_heapfile(hf, atoi(argv[3]), file);
	
	//Get the directory header
	fseek(file, 0, SEEK_SET);
	DirHeader *dirhdr = (DirHeader *) malloc(sizeof(DirHeader));
	fread(dirhdr, sizeof(DirHeader), 1, hf->file_ptr);
	hf->dirhdr = dirhdr;
	
	//Make sure the correct page size is given
	if(dirhdr->page_size != atoi(argv[3])){
		std::cout<<"Page size mismatch";
		fclose(file);
		return -1;
	}

	Record *r;
	int readIndex = 0;
	const char *attribute;
	char *newAttribute;
	int pid;
	int count = 0;
	Page *page = (Page *) malloc(sizeof(Page));
	init_fixed_len_page(page, atoi(argv[3]), num_attributes * byte_per_attribute);
	
	//Go through csv file one line at a time
	std::string line;
	std::ifstream infile(argv[2]);
	std::cout<<dirhdr->num_pages<<std::endl;
	std::cout<<dirhdr->num_dirs<<std::endl;
	std::cout<<dirhdr->page_size<<std::endl;
	while(std::getline(infile, line)){
		r = (Record *) malloc(sizeof(Record));
		//Each record contains 100 strings of size 10 separated by commas
		for(count = 0; count < num_attributes; count++){
			attribute = line.substr(readIndex, byte_per_attribute).c_str();
			newAttribute = (char *) malloc(11);
			newAttribute[10] = '\0';
			memcpy(newAttribute, attribute, 11);
			(*r).push_back(newAttribute);
		    readIndex += byte_per_attribute + 1;
		}
		
		//Page full write it to heapfile and get a new one
		if(add_fixed_len_page(page, r) == -1){
			//Allocate a new page and perform write
			pid = alloc_page(hf);
			write_page(page, hf, pid);
			free(page->data);
			//Get new page
			init_fixed_len_page(page, atoi(argv[3]), num_attributes * byte_per_attribute);
			//Perform write to new page
			add_fixed_len_page(page, r);
		}
		readIndex = 0;
	}
	//Write the final page into disk
	pid = alloc_page(hf);
	write_page(page, hf, pid);
	
	//Update the directory header
	fseek(file, 0, SEEK_SET);
	fwrite(dirhdr, sizeof(DirHeader), 1, file);
	
	std::cout<<dirhdr->num_pages<<std::endl;
	std::cout<<dirhdr->num_dirs<<std::endl;
	std::cout<<dirhdr->page_size<<std::endl;
	
	free(page);
	fclose(file);
	infile.close();
}