#include <stdio.h>
#include <string.h>  
#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>
#include "ext2.h"

 //Returns the index into the inode bitmap of a free node and updates sb and gd
 //Returns -1 if none are free
 int getFreeInode(struct ext2_super_block * sb,
     struct ext2_group_desc * gd, char * inodeMap) {

     int count;
     int count2;
     int x;
     int counter = -1;
     int inode = 0;
     for (count = 0; count < (sb - > s_inodes_count / 8); count++) {
         x = 1;
         for (count2 = 0; count2 < 8; count2++) {
             inode++;
             if (!(inodeMap[count] & x)) {
                 counter = count;
                 count = sb - > s_inodes_count / 8;
                 break;
             }
             x = x * 2;
         }
     }
     //Update metadata
     inodeMap[counter] = inodeMap[counter] | x;
     sb - > s_free_inodes_count = sb - > s_free_inodes_count - 1;
     gd - > bg_free_inodes_count = gd - > bg_free_inodes_count - 1;
     return inode;
 }

 //Returns the index into the block bitmap of a free block and updates the sb and gd
 //Returns -1 if none are free
 int getFreeBlock(struct ext2_super_block * sb,
     struct ext2_group_desc * gd, char * blockMap) {

     int count;
     int count2;
     int x;
     int counter = -1;
     int block = 0;
     for (count = 0; count < (sb - > s_blocks_count / 8); count++) {
         x = 1;
         for (count2 = 0; count2 < 8; count2++) {
             block++;
             if (!(blockMap[count] & x)) {
                 counter = count;
                 count = sb - > s_blocks_count / 8;
                 break;
             }
             x = x * 2;
         }
     }
     //Update metadata
     blockMap[counter] = blockMap[counter] | x;
     sb - > s_free_blocks_count = sb - > s_free_blocks_count - 1;
     gd - > bg_free_blocks_count = gd - > bg_free_blocks_count - 1;
     return block;
 }

 //Returns the inode number of the given path
 //Returns -1 if not found
 int getInodeOfPath(char * path, unsigned char * disk,
     struct ext2_group_desc * gd, struct ext2_super_block * sb) {

     struct ext2_inode * inodeTable = (struct ext2_inode * )(disk +
         EXT2_BLOCK_SIZE * gd - > bg_inode_table);

     struct ext2_inode currInode = inodeTable[1]; //Start at root
     struct ext2_dir_entry * dir;
     char fileName[EXT2_NAME_LEN];
     int offset = 0;
     int length = strlen(path) + 1;
     //Copy the given path so it is not consumed
     char copiedPath[length];
     strcpy(copiedPath, path);
     char * pathDirectory = strtok(copiedPath, "/");
     char prevDir[EXT2_NAME_LEN];
     int maxIndex = 0;
     int count = 0;

     dir = (struct ext2_dir_entry * )(disk + 1024 * currInode.i_block[0]);
     pathDirectory = strtok(NULL, "/");

     while (pathDirectory != NULL) {
         strcpy(prevDir, pathDirectory);
         pathDirectory = strtok(NULL, "/");
         maxIndex = currInode.i_blocks / (2 << sb - > s_log_block_size);
         count = 0;
         //Check every block
         while (count < maxIndex) {
             dir = (struct ext2_dir_entry * )(disk + 1024 *
                 currInode.i_block[count]);

             //Check all files in directory
             //Keep going until the end of block is reached
             while (offset < EXT2_BLOCK_SIZE) {
                 strncpy(fileName, dir - > name, dir - > name_len);
                 fileName[dir - > name_len] = '\0';
                 offset = offset + dir - > rec_len;
                 //We found the directory if the names are the same and it's a directory
                 if (strcmp(fileName, prevDir) == 0) {
                     if (pathDirectory != NULL && dir - > file_type != 2) {
                         return -1;
                     } else {
                         break;
                     }
                 }
                 dir = (struct ext2_dir_entry * )(disk + EXT2_BLOCK_SIZE *
                     currInode.i_block[count] + offset);
             }
             offset = 0;
             count++;
         }
         //Not found
         if (strcmp(fileName, prevDir) != 0) {
             return -1;
         }
         currInode = inodeTable[dir - > inode - 1];
     }
     return dir - > inode;
 }

 //Returns the inode number of the second last part of the path
 //Returns -1 if not found
 int getInodeOfPartialPath(char * path, unsigned char * disk,
     struct ext2_group_desc * gd, struct ext2_super_block * sb) {

     struct ext2_inode * inodeTable = (struct ext2_inode * )(disk +
         EXT2_BLOCK_SIZE * gd - > bg_inode_table);

     struct ext2_inode currInode = inodeTable[1]; //Start at root
     struct ext2_dir_entry * dir;
     char fileName[EXT2_NAME_LEN];
     int offset = 0;
     int length = strlen(path);
     int counter = 0;
     int finalLocation = 0;
     char currChar = '1';
     int maxIndex = 0;
     int count = 0;
     //Get the path up until the second last part
     //Ex. ./d1/d2/d3 will be ./d1/d2/
     while (currChar != '\0') {
         counter++;
         currChar = path[counter];
         if (currChar == '/') {
             finalLocation = counter;
         }
     }
     char copiedPath[finalLocation + 2];
     strncpy(copiedPath, path, finalLocation + 1);
     copiedPath[finalLocation + 1] = '\0';

     char * pathDirectory = strtok(copiedPath, "/");
     char prevDir[EXT2_NAME_LEN];

     dir = (struct ext2_dir_entry * )(disk + 1024 * currInode.i_block[0]);
     pathDirectory = strtok(NULL, "/");

     while (pathDirectory != NULL) {
         strcpy(prevDir, pathDirectory);
         pathDirectory = strtok(NULL, "/");
         maxIndex = currInode.i_blocks / (2 << sb - > s_log_block_size);
         count = 0;
         //Check every block
         while (count < maxIndex) {
             dir = (struct ext2_dir_entry * )(disk + 1024 *
                 currInode.i_block[count]);
             //Check all files in directory
             //Keep going until the end of block is reached
             while (offset < EXT2_BLOCK_SIZE) {
                 strncpy(fileName, dir - > name, dir - > name_len);
                 fileName[dir - > name_len] = '\0';
                 offset = offset + dir - > rec_len;
                 //We found the directory if the names are the same and 
                 //it's a directory
                 if (strcmp(fileName, prevDir) == 0) {
                     break;
                 }
                 dir = (struct ext2_dir_entry * )(disk + EXT2_BLOCK_SIZE *
                     currInode.i_block[count] + offset);
             }
             //Break out of second loop
             if (strcmp(fileName, prevDir) == 0) {
                 break;
             }
             offset = 0;
             count++;
         }
         //Not found
         if (strcmp(fileName, prevDir) != 0) {
             return -1;
         }
         //Found a file not a directory
         if (dir - > file_type == 1) {
             return -1;
         }
         currInode = inodeTable[dir - > inode - 1];
     }
     return dir - > inode;
 }

 //Returns -1 if a file with a given name is not found in the
 //directory of dirInode. Returns the inode of the file if it
 //is found
 int doesFileExist(int dirInode, char * name, unsigned char * disk,
     struct ext2_group_desc * gd, struct ext2_super_block * sb) {

     struct ext2_inode * inodeTable = (struct ext2_inode * )(disk +
         EXT2_BLOCK_SIZE * gd - > bg_inode_table);

     struct ext2_inode currInode = inodeTable[dirInode - 1];
     int offset = 0;
     struct ext2_dir_entry * dir;
     char fileName[EXT2_NAME_LEN];
     int maxIndex = 0;
     int count = 0;
     maxIndex = currInode.i_blocks / (2 << sb - > s_log_block_size);

     //Look through all files in the directory
     while (count < maxIndex) {
         while (offset < EXT2_BLOCK_SIZE) {
             dir = (struct ext2_dir_entry * )(disk + EXT2_BLOCK_SIZE *
                 currInode.i_block[count] + offset);

             strncpy(fileName, dir - > name, dir - > name_len);
             fileName[dir - > name_len] = '\0';
             //We found the file
             if (strcmp(fileName, name) == 0) {
                 return dir - > inode;
             }
             offset = offset + dir - > rec_len;
         }
         count++;
     }
     return -1;
 }

 //Returns the final part of the path name
 //EX. ./d1/d2/d3 as input will return d3
 char * getFileName(char * name) {
     char * retName = malloc(sizeof(char) * EXT2_NAME_LEN + 1);
     char * nextName;
     int length = strlen(name) + 1;
     //Copy given name to perform strtok without mutating input
     char path[length];
     strcpy(path, name);

     nextName = strtok(path, "/");
     while (nextName != NULL) {
         strcpy(retName, nextName);
         nextName = strtok(NULL, "/");
     }
     return retName;
 }

 //Adds an entry into the directory entry
 int addEntry(unsigned int inode, char * name, unsigned char * disk,
     struct ext2_inode dirInode, unsigned char type,
     struct ext2_super_block * sb) {

     struct ext2_dir_entry * dir = (struct ext2_dir_entry * )(disk +
         EXT2_BLOCK_SIZE * dirInode.i_block[0]);

     int name_len = strlen(name);
     struct ext2_dir_entry * newDir = malloc(sizeof(struct ext2_dir_entry * ));
     int offset = 0;
     int offset2;
     int foundRecLen = 0;
     //Size of the record being added
     int size = 8 + name_len;
     //The maximum index of the inodes i_blocks array
     int maxIndex = dirInode.i_blocks / (2 << sb - > s_log_block_size);
     int count = 0;
     //check all blocks for the directory
     while (count < maxIndex) {
         //Find the final entry
         while (offset < EXT2_BLOCK_SIZE) {
             dir = (struct ext2_dir_entry * )((disk + EXT2_BLOCK_SIZE *
                 dirInode.i_block[count]) + offset);

             offset2 = offset;
             offset = offset + dir - > rec_len;
             foundRecLen = 8 + dir - > name_len;
         }
         while (foundRecLen % 4 != 0) {
             foundRecLen++;
         }
         //There is enough room after this entry
         if ((EXT2_BLOCK_SIZE - offset2 - foundRecLen) > size) {
             dir - > rec_len = foundRecLen;
             dir = (struct ext2_dir_entry * )((disk + EXT2_BLOCK_SIZE *
                 dirInode.i_block[count]) + offset2 + foundRecLen);

             dir - > rec_len = EXT2_BLOCK_SIZE - offset2 - foundRecLen;
             dir - > inode = inode;
             dir - > name_len = name_len;
             dir - > file_type = type;
             strncpy(dir - > name, name, name_len);
             return 0;
         }
         offset = 0;
         count++;
     }
     return -1;
 }

 //Allocates a new block to the given directory
 int allocateNewBlock(int inode, unsigned char * disk,
     struct ext2_group_desc * gd, struct ext2_super_block * sb) {

     struct ext2_inode * inodeTable = (struct ext2_inode * )(disk +
         EXT2_BLOCK_SIZE * gd - > bg_inode_table);

     struct ext2_inode dirInode = inodeTable[inode - 1];
     char * blockMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_block_bitmap);
     int blockNum = getFreeBlock(sb, gd, blockMap);
     int maxIndex = dirInode.i_blocks / (2 << sb - > s_log_block_size);
     dirInode.i_block[maxIndex] = blockNum;
     dirInode.i_blocks = dirInode.i_blocks + 2;
     inodeTable[inode - 1] = dirInode;
     return 0;
 }
