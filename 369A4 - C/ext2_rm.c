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
    char * usage = "USAGE: ext2_rm <Formatted virtual disk> <Path to file/link on disk to remove>\n";
    unsigned char * disk;
    //Group descriptor which is located on block 2
    struct ext2_group_desc * gd;
    struct ext2_super_block * sb;
    //Inode table for finding inodes
    struct ext2_inode * inodeTable;
    struct ext2_inode currInode;
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
    int rec_len = 0;
    unsigned int blockToRemove;
    int x;
    int count2;
    //Loops for updating bitmaps for inodes and blocks
    int loop1;
    int loop2;
    struct ext2_dir_entry * dir;

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
    fileInode = doesFileExist(partialPathInode, name, disk, gd, sb);
    //file we are trying to remove does not exist
    if (fileInode == -1) {
        return ENOENT;
    }
    currInode = inodeTable[partialPathInode - 1];
    maxIndex = currInode.i_blocks / (2 << sb - > s_log_block_size);

    while (count < maxIndex) {
        //Find the file we are removing
        while (offset < EXT2_BLOCK_SIZE) {
            dir = (struct ext2_dir_entry * )((disk + EXT2_BLOCK_SIZE *
                currInode.i_block[count]) + offset);

            if (strcmp(dir - > name, name) == 0) {
                rec_len = dir - > rec_len;
                break;
            }
            offset2 = offset;
            offset = offset + dir - > rec_len;
        }
        if (strcmp(dir - > name, name) == 0) {
            break;
        }
        offset = 0;
        count++;
    }
    if (dir - > file_type == EXT2_FT_DIR) {
        return EISDIR;
    }
    //go to the entry before the file we're removing and add the rec_len
    dir = (struct ext2_dir_entry * )((disk + EXT2_BLOCK_SIZE *
        currInode.i_block[count]) + offset2);

    dir - > rec_len = dir - > rec_len + rec_len;

    //Update inode for removed file
    currInode = inodeTable[fileInode - 1];
    currInode.i_links_count = currInode.i_links_count - 1;
    //Remove if there are no more links to it
    if (currInode.i_links_count == 0) {
        currInode.i_dtime = 1;
        maxIndex = currInode.i_blocks / (2 << sb - > s_log_block_size);
        count = 0;
        blockMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_block_bitmap);
        inodeMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_inode_bitmap);
        //Update blocks
        while (count < maxIndex) {
            x = 1;
            count2 = 1;
            //No indirect blocks yet
            if (count < 12) {
                blockToRemove = currInode.i_block[count] - 1;
            }
            //Indirect inode block
            //else{
            //	blockToRemove = (unsigned int)(disk + EXT2_BLOCK_SIZE * currInode.i_block[count] + 4 * (count - 12));
            //	blockToRemove = blockToRemove - 1;
            //}
            loop1 = (blockToRemove + 1) % 8;
            if (loop1 == 0) {
                loop1 = 8;
            }
            while (count2 < loop1) {
                x = x * 2;
                count2++;
            }
            blockMap[blockToRemove / 8] = blockMap[blockToRemove / 8] ^ x;
            sb - > s_free_blocks_count = sb - > s_free_blocks_count + 1;
            gd - > bg_free_blocks_count = gd - > bg_free_blocks_count + 1;
            count++;
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
        inodeMap[(fileInode - 1) / 8] = inodeMap[(fileInode - 1) / 8] ^ x;
        sb - > s_free_inodes_count = sb - > s_free_inodes_count + 1;
        gd - > bg_free_inodes_count = gd - > bg_free_inodes_count + 1;
        inodeTable[fileInode - 1] = currInode;
    }
    return (0);
}
