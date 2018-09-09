#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>
#include "ext2.h"
#include "ext2_utils.h"

unsigned char *disk;


int main(int argc, char ** argv) {

    char * blockMap;
    char * inodeMap;
    struct ext2_inode * inodeTable;
    struct ext2_inode inode;
    int maxIndex;
    char type;
    //Iterate through values in blockMap
    int count;
    //Iterate through bits in values
    int count2;
    //Iterate through inode table
    int count3;
    //Iterating through inode table again
    int count4;
    //The directory entry
    struct ext2_dir_entry * dir;
    int total_rec_len = 0;
    int offset = 0;
    char fileName[EXT2_NAME_LEN];
    unsigned int x;
    if (argc != 2) {
        fprintf(stderr, "Usage: readimg <image file name>\n");
        exit(1);
    }
    int fd = open(argv[1], O_RDWR);

    disk = mmap(NULL, 128 * 1024, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (disk == MAP_FAILED) {
        perror("mmap");
        exit(1);
    }

    struct ext2_super_block * sb = (struct ext2_super_block * )(disk + 1024);
    struct ext2_group_desc * gd = (struct ext2_group_desc * )(disk + EXT2_BLOCK_SIZE * 2);
    printf("Inodes: %d\n", sb - > s_inodes_count);
    printf("Blocks: %d\n", sb - > s_blocks_count);
    printf("    block bitmap: %d\n", gd - > bg_block_bitmap);
    printf("    inode bitmap: %d\n", gd - > bg_inode_bitmap);
    printf("    inode table: %d\n", gd - > bg_inode_table);
    printf("    free blocks: %d\n", gd - > bg_free_blocks_count);
    printf("    free inodes: %d\n", gd - > bg_free_inodes_count);
    printf("    use_dirs: %d\n", gd - > bg_used_dirs_count);
    blockMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_block_bitmap);
    inodeMap = (char * )(disk + EXT2_BLOCK_SIZE * gd - > bg_inode_bitmap);
    printf("Block bitmap: ");
    for (count = 0; count < sb - > s_blocks_count / 8; count++) {
        x = 1;
        for (count2 = 0; count2 < 8; count2++) {
            if (blockMap[count] & x) {
                printf("1");
            } else {
                printf("0");
            }
            x = x * 2;
        }
        printf(" ");
    }
    printf("\n");

    printf("Inode bitmap: ");
    for (count = 0; count < sb - > s_inodes_count / 8; count++) {
        x = 1;
        for (count2 = 0; count2 < 8; count2++) {
            if (inodeMap[count] & x) {
                printf("1");
            } else {
                printf("0");
            }
            x = x * 2;
        }
        printf(" ");
    }
    printf("\n");
    printf("\nInodes:");
    inodeTable = (struct ext2_inode * )(disk + EXT2_BLOCK_SIZE * gd - > bg_inode_table);
    for (count3 = 0; count3 < sb - > s_inodes_count; count3++) {
        inode = inodeTable[count3];
        if (inode.i_mode & EXT2_S_IFDIR) {
            type = 'd';
        } else if (inode.i_mode & EXT2_S_IFREG) {
            type = 'f';
        } else if (inode.i_mode & EXT2_S_IFLNK) {
            type = 's';
        }
        if (count3 == 1 || ((count3 >= 11) && (inode.i_size > 0))) {
            maxIndex = inode.i_blocks / (2 << sb - > s_log_block_size);
            printf("\n iBlocks: %d\n", inode.i_blocks);
            printf("\n[%d] type: %c size: %d links: %d blocks: %d\n",
                count3 + 1, type, inode.i_size, inode.i_links_count, inode.i_blocks);

            printf("[%d] Blocks:", count3 + 1);
            for (count = 0; count < maxIndex; count++) {
                printf(" %d", inode.i_block[count]);
            }
        }
    }

    printf("\n\nDirectory Blocks:");
    for (count4 = 0; count4 < sb - > s_inodes_count; count4++) {
        inode = inodeTable[count4];
        if ((inode.i_mode & EXT2_S_IFDIR) && (count4 == 1 || ((count4 >= 11) && (inode.i_size > 0)))) {
            printf("\n    DIR BLOCK NUM: %d (for inode %d)", inode.i_block[0], count4 + 1);
            dir = (struct ext2_dir_entry * )(disk + 1024 * inode.i_block[0]);
            while (total_rec_len < EXT2_BLOCK_SIZE) {
                if (dir - > file_type == 2) {
                    type = 'd';
                } else if (dir - > file_type == 1) {
                    type = 'f';
                } else if (dir - > file_type == 7) {
                    type = 's';
                }
                strncpy(fileName, dir - > name, dir - > name_len);
                fileName[dir - > name_len] = '\0';
                printf("\nInode: %d rec_len: %d name_len: %d type= %c name= %s",
                    dir - > inode, dir - > rec_len, dir - > name_len, type, fileName);
                total_rec_len = total_rec_len + dir - > rec_len;
                offset = offset + dir - > rec_len;
                dir = (struct ext2_dir_entry * )(disk + 1024 * inode.i_block[0] + offset);
            }
        }
        offset = 0;
        total_rec_len = 0;
    }

    int test = getInodeOfPartialPath("./", disk, gd, sb);
    int test2 = getInodeOfPath("./level1", disk, gd, sb);
    printf("\n%d\n", test);
    printf("\n%d\n", test2);
    /**int test3 = doesFileExist(11, "level2", disk, gd);
    printf("\n%d\n", test3);
    char *test4 = getFileName("./");
    printf("%s\n", test4);**/
    return 0;
}
