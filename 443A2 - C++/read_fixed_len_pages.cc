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
	if(argc < 3){
		printf("Not enough Arguments\n");
		return -1;
	}
	//Loop variables
	int count1 = 0;
	int count2 = 0;
	int count3 = 0;
	
	std::string line;
	FILE *file = fopen(argv[1], "r");
	//Stores the page that was read
	Page *p = (Page *) malloc(sizeof(Page));
	init_fixed_len_page(p, atoi(argv[2]), num_attributes * byte_per_attribute);
	char *word;

	struct timeb t;
    ftime(&t);
	long start = t.time * 1000 + t.millitm;
	
	
	//Read each page one at a time
	while(fread(p->data, 1, atoi(argv[2]), file) != 0){
		char *bytemap = p->data;
		//Go through every record in the page
		for(count1 = 0; count1 < p->page_size / (num_attributes * byte_per_attribute); count1++){
			word = (char *) (((char *)p->data) + (num_attributes * byte_per_attribute) * count1);
			//Print every string in record one at a time
			for(count2 = 0; count2 < num_attributes; count2++){
				//Print each char one at a time
				for(count3 = 0; count3 < byte_per_attribute; count3++){
					std::cout << word[count3];
				}
				word = word + byte_per_attribute;
				if(count2 < 99){
					std::cout << ",";
				}
			}
			std::cout << "\n";
		}
	}
	ftime(&t);
	long finish = t.time * 1000 + t.millitm;
	std::cout << "TIME: " << finish - start << "milliseconds"<< "\n";
	return 0;
}