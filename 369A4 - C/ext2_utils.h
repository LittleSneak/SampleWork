#ifndef __EXT2_UTILS_H__
#define __EXT2_UTILS_H__

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include "ext2.h"

//Obtains the first free inode in the inodemap
extern int getFreeInode(struct ext2_super_block *sb, 
    struct ext2_group_desc *gd, char *inodeMap);

//Returns the first free block in the blockmap
extern int getFreeBlock(struct ext2_super_block *sb, 
    struct ext2_group_desc *gd, char *blockMap);

//Returns the inode of the path
extern int getInodeOfPath(char *path, unsigned char *disk, 
    struct ext2_group_desc *gd, struct ext2_super_block *sb);

//Returns the inode of the path's second last part
//ex. ./d1/d2/d3 as input will return the inode of
// ./d1/d2
extern int getInodeOfPartialPath(char *path, unsigned char *disk, 
    struct ext2_group_desc *gd, struct ext2_super_block *sb);

//Given the inode of the directory, returns the entry in that
//directory with the given name. -1 if not found.
extern int doesFileExist(int dirInode, char *name, unsigned char *disk, 
    struct ext2_group_desc *gd, struct ext2_super_block *sb);

//returns the filename of a path
//EX. ./t1/t2/t3 as input will yield t3 as output
extern char *getFileName(char *name);

//Adds an entry to a given directory
//inode if the inode number of the new entry
//dirInode is the inode of the directory
//type is the type of the new entry
extern int addEntry(unsigned int inode, char *name, unsigned char *disk, 
    struct ext2_inode dirInode, unsigned char type, struct ext2_super_block *sb);

//Allocates a new block for a given directory
extern int allocateNewBlock(int inode, unsigned char *disk, 
    struct ext2_group_desc *gd, struct ext2_super_block *sb);
#endif