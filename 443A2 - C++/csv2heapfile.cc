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
		std::cout<<"Arguments: <csv_file> <heapfile> <page_size>";
		return -1;
	}
	//Initialize heapfile
	FILE *file = fopen(argv[2], "wb");
	Heapfile *hf = (Heapfile *) malloc(sizeof(Heapfile));
	init_heapfile(hf, atoi(argv[3]), file);
	
	//Get the directory header
	DirHeader *dirhdr = (DirHeader *) malloc(sizeof(DirHeader));
	dirhdr->num_dirs = 0;
	dirhdr->num_pages = 0;
	dirhdr->page_size = atoi(argv[3]);
	
	//Put dirheader on heapfile
	hf->dirhdr = dirhdr;

	Record *r;
	const char *attribute;
	char *newAttribute;
	int pid;
	int recordCount = 0;
	int pageCount = 1;
	int readIndex = 0;
	int count = 0;
	Page *page = (Page *) malloc(sizeof(Page));
	init_fixed_len_page(page, atoi(argv[3]), num_attributes * byte_per_attribute);
	
	struct timeb t;
    ftime(&t);
	long start = t.time * 1000 + t.millitm;
	
	//Go through csv file one line at a time
	std::string line;
	std::ifstream infile(argv[1]);
	
	while(std::getline(infile, line)){
		r = (Record *) malloc(sizeof(Record));
		recordCount++;
		//Each record contains 100 strings of size 10 separated by commas
		for(count = 0; count < num_attributes; count++){
			attribute = line.substr(readIndex, byte_per_attribute).c_str();
			newAttribute = (char *) malloc(11);
			newAttribute[10] = '\0';
			memcpy(newAttribute, attribute, 11);
			(*r).push_back(newAttribute);
		    readIndex += byte_per_attribute + 1;
		}
		//writePage(page);
		//Page full write it to heapfile and get a new one
		if(add_fixed_len_page(page, r) == -1){
			pageCount++;
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
	
	ftime(&t);
	long finish = t.time * 1000 + t.millitm;
	std::cout << "NUMBER OF RECORDS: " << recordCount << "\n";
	std::cout << "NUMBER OF PAGES: " << pageCount << "\n";
	std::cout << "TIME: " << finish - start << "milliseconds"<< "\n";
	free(page);
	fclose(file);
	infile.close();
}