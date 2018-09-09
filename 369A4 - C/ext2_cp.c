#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <errno.h>
#include "ext2.h"
#include "ext2_utils.h"
 
int main(int argc, char * argv[]) {
    FILE * tfp = stdin;
    char * usage = "USAGE: ext2_cp <Formatted virtual disk> <Path to file on OS> <Path to location on img> \n";
    unsigned char * disk;
    //Group descriptor which is located on block 2
    struct ext2_group_desc * gd;
    struct ext2_super_block * sb;
    //Inode table for finding inodes
    struct ext2_inode * inodeTable;
    //Holds filename being added
    char * fileName;
    char * inodeMap;
    char * blockMap;
    //A buffer for reading the file from OS
    char * buffer = malloc(sizeof(char) * 1024);
    int numRead;
    //The inode that will be added to the inode table
    struct ext2_inode newInode;
    int freeInode = 0;
    int freeBlock = 0;
    //Variables for copying path
    int pathInode;
    int iNodeBlockCount = 0;
    int size = 0;
    int numBlocks = 0;
    int finalInode;
    int addEntryReturn;
    //Print error if there aren't enough arguments
    if (argc < 4) {
        fprintf(stderr, "%s", usage);
        exit(1);
    }
    //Try reading the file on OS
    if ((tfp = fopen(argv[2], "r")) == NULL) {
        perror("Error opening file on OS");
        return ENOENT;
    }
    //Try reading the disk
    int fd = open(argv[1], O_RDWR);
    disk = mmap(NULL, 128 * 1024, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (disk == MAP_FAILED) {
        perror("mmap failure");
        return ENOENT;
    }
    gd = (struct ext2_group_desc * )(disk + EXT2_BLOCK_SIZE * 2);
    sb = (struct ext2_super_block * )(disk + 1024);

    inodeTable = (struct ext2_inode * )(disk + EXT2_BLOCK_SIZE *
        gd - > bg_inode_table);

    //Find the directory:
    pathInode = getInodeOfPartialPath(argv[3], disk, gd, sb);
    if (pathInode == -1 || pathInode == -2) {
        return ENOENT;
    }

    fileName = getFileName(argv[3]);
    finalInode = doesFileExist(pathInode, fileName, disk, gd, sb);
    //We found the inode for the final part
    if (finalInode != -1) {
        //Final inode is a directory so we use the filename given by the
        //OS path
        if (inodeTable[finalInode - 1].i_mode & EXT2_S_IFDIR) {
            fileName = getFileName(argv[2]);
            pathInode = finalInode;
            //Check if that filename is already in there
            if (doesFileExist(pathInode, fileName, disk, gd, sb) != -1) {
                return EEXIST;
            }
        }
        //It was a file
        else {
            return EEXIST;
        }
    }
    //Final inode not found
    else {
        //We should have found a directory if it ended with /
        if (argv[3][strlen(argv[3]) - 1] == '/') {
            return ENOENT;
        }
    }
    //Otherwise, the filename we are using will not be changed and
    //we will adding it to the directory

    //Find an available inode
    inodeMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_inode_bitmap);
    freeInode = getFreeInode(sb, gd, inodeMap);
    newInode = inodeTable[freeInode - 1];
    newInode.i_mode = EXT2_S_IFREG;
    newInode.i_uid = 0;
    newInode.i_gid = 0;
    newInode.i_links_count = 1;
    newInode.i_dtime = 0;
    newInode.i_ctime = 1;
    blockMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_block_bitmap);

    //Copy the file
    while ((numRead = fread(buffer, 1, 1024, tfp)) != 0) {
        freeBlock = getFreeBlock(sb, gd, blockMap);
        //Add the block to the inode
        newInode.i_block[iNodeBlockCount] = freeBlock;
        //Write to disk img
        memcpy(disk + EXT2_BLOCK_SIZE * freeBlock, buffer, EXT2_BLOCK_SIZE);
        iNodeBlockCount++;
        size = size + numRead;
        numBlocks++;
    }
    newInode.i_size = size;
    newInode.i_blocks = 2 * numBlocks;
    //Add entry
    addEntryReturn = addEntry(freeInode, fileName, disk,
        inodeTable[pathInode - 1], EXT2_FT_REG_FILE, sb);

    if (addEntryReturn == -1) {
        allocateNewBlock(pathInode, disk, gd, sb);

        addEntryReturn = addEntry(freeInode, fileName, disk,
            inodeTable[pathInode - 1], EXT2_FT_REG_FILE, sb);
    }
    inodeTable[freeInode - 1] = newInode;
    return (0);
}
