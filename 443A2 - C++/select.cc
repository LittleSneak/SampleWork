#include <fstream>
#include <stdio.h>
#include <string>
#include <iostream>
#include <sys/timeb.h>
#include <cstring>
#include "library.h"
#include <stdlib.h>

//Finds all records that match the given query
//Also logs how long the query takes to complete
int main(int argc, char *argv[]){
	if(argc < 6){
		std::cout<<"Arguments: <heapfile> <attribute_id> <start> <end> <page_size>\n";
		return -1;
	}
	
	//Init heapfile
	FILE *file = fopen(argv[1], "r+");
	Heapfile *hf = (Heapfile *) malloc(sizeof(Heapfile));
	init_heapfile(hf, atoi(argv[5]), file);
	
	//Get the directory header
	fseek(file, 0, SEEK_SET);
	DirHeader *dirhdr = (DirHeader *) malloc(sizeof(DirHeader));
	fread(dirhdr, sizeof(DirHeader), 1, hf->file_ptr);
	
	if(dirhdr->page_size != atoi(argv[5])){
		std::cout<<"Page size mismatch\n";
		fclose(file);
		return -1;
	}
	
	hf->dirhdr = dirhdr;
	
	//Iterate through every record
	RecordIterator it(hf);
	
	Record *r;
	int x = 0;
	int attribute = atoi(argv[2]);
	std::string start = argv[3];
	std::string end = argv[4];
	
	//Time how long it takes
	struct timeb t;
    ftime(&t);
	long startTime = t.time * 1000 + t.millitm;
	
	//Print out all strings that match query
	while(it.hasNext()){
		r = it.next();
		if((*r).at(attribute) >= start && (*r).at(attribute) <= end){
			std::cout<<(*r).at(attribute)[0];
			std::cout<<(*r).at(attribute)[1];
			std::cout<<(*r).at(attribute)[2];
			std::cout<<(*r).at(attribute)[3];
			std::cout<<(*r).at(attribute)[4];
			std::cout<<std::endl;
		}
	}
	
	ftime(&t);
	long finish = t.time * 1000 + t.millitm;
	std::cout << "TIME: " << finish - startTime << " milliseconds"<< "\n";
	fclose(file);
	return 0;
}