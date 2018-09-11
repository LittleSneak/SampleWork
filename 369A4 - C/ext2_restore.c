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
    char * usage = "USAGE: ext2_restore <Formatted virtual disk> <Path to file/link on disk to restore>\n";
    unsigned char * disk;
    //Group descriptor which is located on block 2
    struct ext2_group_desc * gd;
    struct ext2_super_block * sb;
    //Inode table for finding inodes
    struct ext2_inode * inodeTable;
    //Holds the inode of the directory
    struct ext2_inode currInode;
    //Holds the inode of the file being restored
    struct ext2_inode oldInode;
    char * inodeMap;
    char * blockMap;
    //Variables for copying path
    int partialPathInode;
    //inode of file being removed
    int fileInode;
    //name of file being removed
    char * name;
    //Offsets for finding the file in the directory entry
    int offset = 0;
    int offset2 = 0;
    //Maximum index of block array in inode
    int maxIndex;
    int count = 0;
    int count3 = 0;
    int rec_len = 0;
    int prev_dir_rec_len = 0;
    struct ext2_dir_entry * dir;
    int blockToCheck;
    int loop1;
    int loop2;
    int count2 = 0;
    int blockToAdd;
    int x;
    int restorePoint = 0;

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
    //We are only removing files
    if (argv[2][strlen(argv[2]) - 1] == '/') {
        return EISDIR;
    }

    gd = (struct ext2_group_desc * )(disk + EXT2_BLOCK_SIZE * 2);
    sb = (struct ext2_super_block * )(disk + 1024);
    inodeTable = (struct ext2_inode * )(disk + EXT2_BLOCK_SIZE *
        gd - > bg_inode_table);

    partialPathInode = getInodeOfPartialPath(argv[2], disk, gd, sb);
    name = getFileName(argv[2]);
    //The file exists and is not deleted
    if (doesFileExist(partialPathInode, name, disk, gd, sb) != -1) {
        return EEXIST;
    }
    //Find file being restored
    currInode = inodeTable[partialPathInode - 1];
    maxIndex = currInode.i_blocks / (2 << sb - > s_log_block_size);
    while (count < maxIndex) {
        while (offset < EXT2_BLOCK_SIZE) {
            dir = (struct ext2_dir_entry * )((disk + EXT2_BLOCK_SIZE *
                currInode.i_block[count]) + offset);

            if (strcmp(dir - > name, name) == 0) {
                break;
            }
            restorePoint = restorePoint + dir - > rec_len;
            offset2 = offset;
            offset = offset + 8 + dir - > name_len;
            while (offset % 4 != 0) {
                offset++;
            }
            if (dir - > name_len == 0) {
                offset = restorePoint;
            }
        }
        if (strcmp(dir - > name, name) == 0) {
            break;
        }
        offset = 0;
        count++;
    }
    //Could not find file
    if (strcmp(dir - > name, name) != 0) {
        return ENOENT;
    }
    if (dir - > file_type == EXT2_FT_DIR) {
        return EISDIR;
    }
    oldInode = inodeTable[dir - > inode - 1];
    fileInode = dir - > inode;
    //Check if in use
    if (oldInode.i_dtime == 0) {
        return ENOENT;
    }
    //check if blocks are in use
    blockMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_block_bitmap);
    inodeMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_inode_bitmap);
    maxIndex = oldInode.i_blocks / (2 << sb - > s_log_block_size);
    while (count3 < maxIndex) {
        x = 1;
        count2 = 1;
        blockToCheck = oldInode.i_block[count3] - 1;
        loop1 = (blockToCheck + 1) % 8;
        if (loop1 == 0) {
            loop1 = 8;
        }
        while (count2 < loop1) {
            x = x * 2;
            count2++;
        }
        if (blockMap[blockToCheck / 8] & x) {
            return ENOENT;
        }
        count3++;
    }
    //Everything is fine, restore file
    //Update blockmap
    count3 = 0;
    while (count3 < maxIndex) {
        x = 1;
        count2 = 1;
        blockToAdd = oldInode.i_block[count3] - 1;
        loop1 = (blockToAdd + 1) % 8;
        if (loop1 == 0) {
            loop1 = 8;
        }
        while (count2 < loop1) {
            x = x * 2;
            count2++;
        }
        blockMap[blockToAdd / 8] = blockMap[blockToAdd / 8] | x;
        sb - > s_free_blocks_count = sb - > s_free_blocks_count - 1;
        gd - > bg_free_blocks_count = gd - > bg_free_blocks_count - 1;
        count3++;
    }
    //Update inodemap
    x = 1;
    count2 = 1;
    loop2 = fileInode % 8;
    if (loop2 == 0) {
        loop2 = 8;
    }
    while (count2 < loop2) {
        x = x * 2;
        count2++;
    }
    inodeMap[(fileInode - 1) / 8] = inodeMap[(fileInode - 1) / 8] | x;
    sb - > s_free_inodes_count = sb - > s_free_inodes_count - 1;
    gd - > bg_free_inodes_count = gd - > bg_free_inodes_count - 1;

    //Update inode
    oldInode.i_dtime = 0;
    oldInode.i_links_count = 1;
    //Update directory entry
    dir = (struct ext2_dir_entry * )((disk + EXT2_BLOCK_SIZE *
        currInode.i_block[count]) + offset2);

    prev_dir_rec_len = 8 + dir - > name_len;
    while (prev_dir_rec_len % 4 != 0) {
        prev_dir_rec_len++;
    }
    rec_len = dir - > rec_len;
    dir - > rec_len = prev_dir_rec_len;
    dir = (struct ext2_dir_entry * )((disk + EXT2_BLOCK_SIZE *
        currInode.i_block[count]) + offset);

    dir - > rec_len = rec_len - prev_dir_rec_len;
    inodeTable[dir - > inode - 1] = oldInode;
    return (0);
}
