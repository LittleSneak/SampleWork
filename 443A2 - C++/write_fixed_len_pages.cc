#include <fstream>
#include <stdio.h>
#include <string>
#include <iostream>
#include <sys/timeb.h>
#include <cstring>
#include "library.h"
#include <stdlib.h>

int main(int argc, char *argv[]){
	//Check if number of arguments is correct
	if(argc < 4){
		printf("Not enough Arguments\n");
		return -1;
	}
	//Open file to write to
	FILE *file = fopen(argv[2], "w+");
	//Store a record that is read
	Record *r;
	//Contains the page being written to
	Page *page = (Page *) malloc(sizeof(Page));

	init_fixed_len_page(page, atoi(argv[3]), num_attributes * byte_per_attribute);
	
	int readIndex = 0;
	int count;
	int count2;
	int recordCount = 0;
	int pageCount = 1;
	
	//Go through csv file one line at a time
	std::string line;
	std::ifstream infile(argv[1]);
	
	struct timeb t;
    ftime(&t);
	long start = t.time * 1000 + t.millitm;
	
	const char *attribute;
	char *newAttribute;
	
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
		
		/* For debugging and seeing vector values
		for(count = 0; count < 100; count++){
			std::cout<<count<<(*r).at(count)<<std::endl;
		}*/
		
		//Page full write it to disk and get a new one
		if(add_fixed_len_page(page, r) == -1){
			pageCount++;
			fwrite(((char *)page->data) + bytemap_size(page), 1, page->page_size, file);
			free(page->data);
			init_fixed_len_page(page, atoi(argv[3]), num_attributes * byte_per_attribute);
			//Perform write on new page
			add_fixed_len_page(page, r);
		}
		readIndex = 0;
	}
	//Write the final page into disk
	fwrite(((char *)page->data) + bytemap_size(page), 1, page->page_size, file);
	ftime(&t);
	long finish = t.time * 1000 + t.millitm;
	std::cout << "NUMBER OF RECORDS: " << recordCount << "\n";
	std::cout << "NUMBER OF PAGES: " << pageCount << "\n";
	std::cout << "TIME: " << finish - start << "milliseconds"<< "\n";
	free(page);
	fclose(file);
	infile.close();
	return 0;
}