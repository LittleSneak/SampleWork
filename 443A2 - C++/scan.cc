#include <fstream>
#include <stdio.h>
#include <string>
#include <iostream>
#include <sys/timeb.h>
#include <cstring>
#include "library.h"
#include <stdlib.h>

//scans and prints out all records
int main(int argc, char *argv[]){
	if(argc < 3){
		std::cout<<"Arguments: <heapfile> <page_size>";
		return -1;
	}
	//Initialize heapfile
	FILE *file = fopen(argv[1], "r");
	Heapfile *hf = (Heapfile *) malloc(sizeof(Heapfile));
	init_heapfile(hf, atoi(argv[2]), file);
	
	//Get the directory header
	DirHeader *dirhdr = (DirHeader *) malloc(sizeof(DirHeader));
	fread(dirhdr, sizeof(DirHeader), 1, hf->file_ptr);
	hf->dirhdr = dirhdr;
	
	RecordIterator it(hf);
	
	Record *r;
	int x = 0;
	while(it.hasNext()){
		r = it.next();
		for(x = 0; x < 100; x++){
			std::cout<<(*r).at(x)<<",";
		}
		std::cout<<std::endl;
	}
	fclose(file);
}