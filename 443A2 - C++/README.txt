A heapfile created to store records from a database.

Heapfile structure:
At the beginning of a file, there is a directory header which keeps track of the total number of pages and directories in the file. After this, the first directory page and its data pages are stored and then the second directory page and its data pages and so on. 

A directory can hold 1250 pages. Structures called DirEntry are stored sequentially within a directory's page's data. Each instance of this structure corresponds to a page within the directory. It contains the offset to the page from the directory and how many free slots the page has.

Each page's data has a character array at the beginning. This is the same as a bit map except a byte is used instead of a bit. This takes up more memory than using bitmaps but we found it to be easier to implement than a bitmap. After the character array, records are stored sequentially. Each record takes up 1000 bytes since there are 10 characters per attribute and 100 attributes.

Update and Delete:
Update and delete use the page ID and slot number of a record instead of a record ID. Pages are numbered starting from 0 and slots are also numbered starting from 0.

Record iterator:
Our record iterator returns a pointer to the record rather than the record object.