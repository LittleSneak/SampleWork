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
    char * usage = "USAGE: ext2_checker <Formatted virtual disk>\n";
    unsigned char * disk;
    //Group descriptor which is located on block 2
    struct ext2_group_desc * gd;
    struct ext2_super_block * sb;
    //Inode table for finding inodes
    struct ext2_inode * inodeTable;
    //Holds the inode of the directory
    struct ext2_inode currInode;
    char * inodeMap;
    char * blockMap;
    //Variables for iterating through bitmaps
    int count;
    int count2;
    int x;
    //Counters for number of free blocks/inodes
    int freeBlocks = 0;
    int freeInodes = 0;
    int totalFixes = 0;
    //List of inodes for iterating over the entire system
    //There are only 32 inodes at most
    int inodes[32];
    //Head and tail of the list
    int head = 0;
    int tail = 1;
    //Start at the root
    inodes[0] = 2;
    //Inode number of current inode
    int inodeNum;
    //Stores the maximum index of the inode's iblocks array
    int maxIndex;
    int count3;
    int offset;
    struct ext2_dir_entry * dir;
    struct ext2_inode currInode2;
    //For iterating through inodes in part D
    int y;
    int count4;
    int loop2;
    //for iterating through blocks for part E
    int datablocksFixed = 0;
    int maxIndex2;
    int count5;
    int count6;
    int z;
    int loop;

    //Print error if there aren't enough arguments
    if (argc != 2) {
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
    inodeTable = (struct ext2_inode * )(disk + EXT2_BLOCK_SIZE *
        gd - > bg_inode_table);

    inodeMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_inode_bitmap);
    blockMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_block_bitmap);

    //PART A
    //Check block bitmap
    for (count = 0; count < sb - > s_blocks_count / 8; count++) {
        x = 1;
        for (count2 = 0; count2 < 8; count2++) {
            if (!(blockMap[count] & x)) {
                freeBlocks++;
            }
            x = x * 2;
        }
    }

    //check inode bitmap
    for (count = 0; count < sb - > s_inodes_count / 8; count++) {
        x = 1;
        for (count2 = 0; count2 < 8; count2++) {
            if (!(inodeMap[count] & x)) {
                freeInodes++;
            }
            x = x * 2;
        }
    }
    if (freeBlocks != sb - > s_free_blocks_count) {
        printf("Fixed: superblock's free blocks counter was off by %d compared to the bitmap",
            abs(freeBlocks - sb - > s_free_blocks_count));
        totalFixes++;
    }
    if (freeInodes != sb - > s_free_inodes_count) {
        printf("Fixed: superblock's free inodes counter was off by %d compared to the bitmap",
            abs(freeInodes - sb - > s_free_inodes_count));
        totalFixes++;
    }
    if (freeBlocks != gd - > bg_free_blocks_count) {
        printf("Fixed: block group's free blocks counter was off by %d compared to the bitmap",
            abs(freeBlocks - gd - > bg_free_blocks_count));
        totalFixes++;
    }
    if (freeInodes != gd - > bg_free_inodes_count) {
        printf("Fixed: block group's free inodes counter was off by %d compared to the bitmap",
            abs(freeInodes - gd - > bg_free_inodes_count));
        totalFixes++;
    }
    //Perform a BFS to check all inodes
    while (head != tail) {
        inodeNum = inodes[head];
        head++;
        currInode = inodeTable[inodeNum - 1];
        maxIndex = currInode.i_blocks / (2 << sb - > s_log_block_size);
        count3 = 0;
        //Go through all blocks of directory
        while (count3 < maxIndex) {
            offset = 0;
            while (offset < EXT2_BLOCK_SIZE) {
                dir = (struct ext2_dir_entry * )((disk + EXT2_BLOCK_SIZE *
                    currInode.i_block[count3]) + offset);

                //skip if inode has been done
                if (offset < 20) {
                    offset = offset + dir - > rec_len;
                    continue;
                }
                fflush(stdout);
                currInode2 = inodeTable[dir - > inode - 1];
                //PART B
                if ((currInode2.i_mode & EXT2_S_IFLNK) &&
                    (dir - > file_type != EXT2_FT_SYMLINK)) {

                    dir - > file_type = EXT2_FT_SYMLINK;
                    printf("\nFixed: Entry type vs inode mismatch: inode[%d]", dir - > inode);
                    totalFixes++;
                } else if ((currInode2.i_mode & EXT2_S_IFREG) &&
                    !(currInode2.i_mode & EXT2_S_IFLNK) &&
                    (dir - > file_type != EXT2_FT_REG_FILE)) {

                    dir - > file_type = EXT2_FT_REG_FILE;
                    printf("\nFixed: Entry type vs inode mismatch: inode[%d]", dir - > inode);
                    totalFixes++;
                } else if ((currInode2.i_mode & EXT2_S_IFDIR) &&
                    (dir - > file_type != EXT2_FT_DIR)) {

                    dir - > file_type = EXT2_FT_DIR;
                    totalFixes++;
                }
                //PART C
                y = 1;
                count4 = 1;
                loop2 = dir - > inode % 8;
                if (loop2 == 0) {
                    loop2 = 8;
                }
                while (count4 < loop2) {
                    y = y * 2;
                    count4++;
                }
                if (!(inodeMap[(dir - > inode - 1) / 8] & y)) {
                    inodeMap[(dir - > inode - 1) / 8] =
                        inodeMap[(dir - > inode - 1) / 8] | y;

                    printf("Fixed: inode [%d] not marked as in-use", dir - > inode);
                    totalFixes++;
                }
                //PART D
                if (currInode2.i_dtime != 0) {
                    currInode2.i_dtime = 0;
                    inodeTable[dir - > inode - 1] = currInode2;
                    printf("Fixed: valid inode marked for deletion: [%d]",
                        dir - > inode);

                    totalFixes++;
                    sb - > s_free_inodes_count = sb - > s_free_inodes_count - 1;
                    gd - > bg_free_inodes_count = gd - > bg_free_inodes_count - 1;
                }
                //PART E
                maxIndex2 = currInode2.i_blocks / (2 << sb - > s_log_block_size);
                count5 = 0;
                while (count5 < maxIndex2) {
                    z = 1;
                    count6 = 1;
                    //Find appropriate positiion for the bit
                    loop = currInode2.i_block[count5] % 8;
                    if (loop == 0) {
                        loop = 8;
                    }
                    while (count6 < loop) {
                        z = z * 2;
                        count6++;
                    }
                    //Check if the bit is set
                    if (!(blockMap[(currInode2.i_block[count5] - 1) / 8] & z)) {
                        inodeMap[(currInode2.i_block[count5] - 1) / 8] =
                            inodeMap[(currInode2.i_block[count5] - 1) / 8] | z;

                        datablocksFixed++;
                        totalFixes++;
                        sb - > s_free_blocks_count = sb - > s_free_blocks_count - 1;
                        gd - > bg_free_blocks_count = gd - > bg_free_blocks_count - 1;
                    }
                    count5++;
                }
                if (datablocksFixed > 0) {
                    printf("Fixed: %d in-use data blocks not marked in data bitmap for inode: [%d]",
                        datablocksFixed, dir - > inode);
                    datablocksFixed = 0;
                }

                //Add inode to list if it is a directory
                if (currInode2.i_mode & EXT2_S_IFDIR) {
                    inodes[tail] = dir - > inode;
                    tail++;
                }
                offset = offset + dir - > rec_len;
            }
            count3++;
        }
    }
    if (totalFixes == 0) {
        printf("\nNo file system inconsistencies detected!");
    } else {
        printf("\n%d file system inconsistencies repaired!", totalFixes);
    }
    return (0);
}
