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
    char * usage = "USAGE: ext2_ln <Formatted virtual disk> <OPTIONAL -r for symbolic link> <Path to link> <Path to direcory> \n";
    unsigned char * disk;
    //Group descriptor which is located on block 2
    struct ext2_group_desc * gd;
    struct ext2_super_block * sb;
    //Inode table for finding inodes
    struct ext2_inode * inodeTable;
    char * inodeMap;
    char * blockMap;
    //The inode that will be added to the inode table
    struct ext2_inode newInode;
    int freeInode = 0;
    int freeBlock = 0;
    //Variables for copying path
    int pathInode;
    int destinationInode;
    //first path which is where the link file will be created
    char * fPath;
    //Second path which is what we're linking to
    char * sPath;
    //name of link
    char * name;
    char * writeDest;
    //Create hardlink if 0
    int sym = 0;
    int addEntryReturn;

    //Print error if there aren't enough arguments
    if (argc < 4 || argc > 5) {
        fprintf(stderr, "%s", usage);
        exit(1);
    }
    //Try reading the disk
    int fd = open(argv[1], O_RDWR);
    disk = mmap(NULL, 128 * 1024, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (disk == MAP_FAILED) {
        perror("mmap failure");
        return ENOENT;
    }
    if (strcmp(argv[2], "-s") == 0) {
        fPath = argv[3];
        sPath = argv[4];
        sym = 1;
    } else if (argc < 4) {
        fPath = argv[2];
        sPath = argv[3];
        sym = 0;
    } else {
        fprintf(stderr, "%s", usage);
        exit(1);
    }
    if (fPath[strlen(fPath) - 1] == '/') {
        return EISDIR;
    }

    gd = (struct ext2_group_desc * )(disk + EXT2_BLOCK_SIZE * 2);
    sb = (struct ext2_super_block * )(disk + 1024);
    inodeTable = (struct ext2_inode * )(disk + EXT2_BLOCK_SIZE *
        gd - > bg_inode_table);

    //Find directory we are linking to
    pathInode = getInodeOfPath(sPath, disk, gd, sb);
    if (pathInode == -1 || pathInode == -2) {
        return ENOENT;
    }

    //Get location of where to put the link
    destinationInode = getInodeOfPartialPath(sPath, disk, gd, sb);
    if (destinationInode == -1) {
        return ENOENT;
    }
    name = getFileName(fPath);
    if (doesFileExist(destinationInode, name, disk, gd, sb) != -1) {
        return EEXIST;
    }

    //Do hard link
    if (sym == 0) {
        addEntry(pathInode, name, disk,
            inodeTable[destinationInode - 1], EXT2_FT_REG_FILE, sb);

        inodeTable[pathInode - 1].i_links_count++;
    }
    //Do sym link
    else {
        //Find a free block/inode
        inodeMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_inode_bitmap);
        blockMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_block_bitmap);
        freeInode = getFreeInode(sb, gd, inodeMap);
        freeBlock = getFreeInode(sb, gd, blockMap);
        //Initialize values in new inode
        newInode = inodeTable[freeInode - 1];
        newInode.i_mode = EXT2_S_IFLNK;
        newInode.i_mode = newInode.i_mode | EXT2_S_IFREG;
        newInode.i_uid = 0;
        newInode.i_gid = 0;
        newInode.i_links_count = 1;
        newInode.i_dtime = 0;
        newInode.i_ctime = 0;
        newInode.i_block[0] = freeBlock;
        newInode.i_size = EXT2_BLOCK_SIZE;
        newInode.i_blocks = 2;
        //Write the path to the block
        writeDest = (char * )(disk + EXT2_BLOCK_SIZE * freeBlock);
        strcpy(writeDest, sPath);
        //Add blocks to directory if full
        addEntryReturn = addEntry(freeInode, name, disk,
            inodeTable[destinationInode - 1], EXT2_FT_SYMLINK, sb);

        if (addEntryReturn == -1) {
            allocateNewBlock(destinationInode, disk, gd, sb);

            addEntry(freeInode, name, disk,
                inodeTable[destinationInode - 1], EXT2_FT_SYMLINK, sb);
        }

        inodeTable[freeInode - 1] = newInode;
    }
    return (0);
}
