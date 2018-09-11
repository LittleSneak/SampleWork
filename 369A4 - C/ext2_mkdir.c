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
    char * usage = "USAGE: ext2_mkdir <Formatted virtual disk> <Path to directory to create> \n";
    unsigned char * disk;
    //Group descriptor which is located on block 2
    struct ext2_group_desc * gd;
    struct ext2_super_block * sb;
    //Inode table for finding inodes
    struct ext2_inode * inodeTable;
    struct ext2_inode dirInode;
    char * inodeMap;
    char * blockMap;
    //The inode that will be added to the inode table
    struct ext2_inode newInode;
    //Variables for copying path
    int pathInode;
    int newInodeNum;
    int newBlock;
    char * dirName;
    struct ext2_dir_entry * dir;
    int addEntryReturn;
    //Print error if there aren't enough arguments
    if (argc != 3) {
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
    gd = (struct ext2_group_desc * )(disk + EXT2_BLOCK_SIZE * 2);
    sb = (struct ext2_super_block * )(disk + 1024);
    //Find directory to make the new directory
    pathInode = getInodeOfPartialPath(argv[2], disk, gd, sb);
    if (pathInode == -1) {
        return ENOENT;
    }
    //Check if directory we are creating already exists
    dirName = getFileName(argv[2]);
    if (doesFileExist(pathInode, dirName, disk, gd, sb) != -1) {
        return EEXIST;
    }
    //Obtain a free inode and block for the new directory
    inodeTable = (struct ext2_inode * )(disk + EXT2_BLOCK_SIZE *
        gd - > bg_inode_table);

    inodeMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_inode_bitmap);
    newInodeNum = getFreeInode(sb, gd, inodeMap);
    blockMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_block_bitmap);
    newBlock = getFreeBlock(sb, gd, blockMap);
    newInode = inodeTable[newInodeNum - 1];
    newInode.i_mode = EXT2_S_IFDIR;
    newInode.i_uid = 0;
    newInode.i_gid = 0;
    newInode.i_links_count = 1;
    newInode.i_dtime = 0;
    newInode.i_ctime = 1;
    newInode.i_block[0] = newBlock;
    newInode.i_size = EXT2_BLOCK_SIZE;
    newInode.i_blocks = 2;
    //Add root and parent to new directory
    dir = (struct ext2_dir_entry * )(disk + EXT2_BLOCK_SIZE * newBlock);
    strncpy(dir - > name, ".", 1);
    dir - > inode = 2;
    dir - > name_len = 1;
    dir - > file_type = 2;
    dir - > rec_len = 12;

    dir = (struct ext2_dir_entry * )(disk + EXT2_BLOCK_SIZE * newBlock + 12);
    strncpy(dir - > name, "..", 2);
    dir - > inode = pathInode;
    dir - > name_len = 2;
    dir - > file_type = 2;
    dir - > rec_len = 1012;

    dirInode = inodeTable[pathInode - 1];
    addEntryReturn = addEntry(newInodeNum, dirName, disk,
        dirInode, EXT2_FT_DIR, sb);

    //If there is no more room. Allocate a new block
    if (addEntryReturn == -1) {
        allocateNewBlock(pathInode, disk, gd, sb);
        addEntry(newInodeNum, dirName, disk, dirInode, EXT2_FT_DIR, sb);
    }
    inodeTable[newInodeNum - 1] = newInode;
    gd - > bg_used_dirs_count = gd - > bg_used_dirs_count + 1;
    return (0);
}
