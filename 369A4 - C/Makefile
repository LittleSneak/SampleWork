
all: ext2_cp ext2_mkdir ext2_ln ext2_rm ext2_restore ext2_checker
	
ext2_cp: ext2_cp.c ext2_utils.o ext2_utils.h
	gcc -Wall -g -o ext2_cp ext2_utils.o ext2_cp.c
	
ext2_mkdir: ext2_mkdir.c ext2_utils.o ext2_utils.h
	gcc -Wall -g -o ext2_mkdir ext2_utils.o ext2_mkdir.c
	
ext2_ln: ext2_ln.c ext2_utils.o ext2_utils.h
	gcc -Wall -g -o ext2_ln ext2_utils.o ext2_ln.c
	
ext2_rm: ext2_rm.c ext2_utils.o ext2_utils.h
	gcc -Wall -g -o ext2_rm ext2_utils.o ext2_rm.c
	
ext2_restore: ext2_restore.c ext2_utils.o ext2_utils.h
	gcc -Wall -g -o ext2_restore ext2_utils.o ext2_restore.c
	
ext2_checker: ext2_checker.c ext2_utils.o ext2_utils.h
	gcc -Wall -g -o ext2_checker ext2_utils.o ext2_checker.c
	
clean : 
	rm -f *.o ext2_utils ext2_cp ext2_mkdir ext2_rm ext2_checker*~