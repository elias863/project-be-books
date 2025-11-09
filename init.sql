CREATE TABLE IF NOT EXISTS Book(
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    copyright BOOLEAN NOT NULL,
    mediatype VARCHAR(255) NOT NULL,
    downloadcount INT NOT NULL
);-- ENGINE=INNODB CHARACTER SET utf8 COLLATE utf8_unicode_ci; aggiugnere se da errore nella codifica
CREATE TABLE IF NOT EXISTS Author(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birthyear INT NOT NULL,
    deathyear INT NOT NULL
);
CREATE TABLE IF NOT EXISTS BookAuthor(   -- tabella per collegare Book con Author
    book_id INT,
    author_id INT,
    PRIMARY KEY(book_id,author_id),
    CONSTRAINT Constr_BookAuthor_Book_fk
        FOREIGN KEY Book_BookAuthor_fk(book_id)
        REFERENCES Book (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT Constr_BookAuthor_Author_fk
        FOREIGN KEY Author_BookAuthor_fk(author_id)
        REFERENCES Author (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS Translator(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birthyear INT NOT NULL,
    deathyear INT NOT NULL
);
CREATE TABLE IF NOT EXISTS BookTranslator(
    book_id INT,
    translator_id INT,
    PRIMARY KEY(book_id,translator_id),
    CONSTRAINT Constr_BookTranslator_Book_fk
        FOREIGN KEY Book_BookTranslator_fk(book_id)
        REFERENCES Book (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT Constr_BookTranslator_Translator_fk
        FOREIGN KEY Translator_BookTranslator_fk(translator_id)
        REFERENCES Translator (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS Subject(
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject VARCHAR(255) NOT NULL,
    book_id INT,
    CONSTRAINT fk_Book_Subject FOREIGN KEY (book_id) REFERENCES Book(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS Summary(
    id INT AUTO_INCREMENT PRIMARY KEY,
    summary TEXT NOT NULL,
    book_id INT,
    CONSTRAINT fk_Book_Summary FOREIGN KEY (book_id) REFERENCES Book(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS Bookshelf(
    id INT AUTO_INCREMENT PRIMARY KEY,
    bookshelf VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS BookBookshelf(   -- tabella per collegare Book con BookShelf
    book_id INT,
    bookshelf_id INT,
    PRIMARY KEY(book_id,bookshelf_id),
    CONSTRAINT Constr_BookBookshelf_Book_fk
        FOREIGN KEY Book_BookBookshelf_fk(book_id)
        REFERENCES Book (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT Constr_BookBookshelf_Bookshelf_fk
        FOREIGN KEY Bookshelf_BookBookshelf_fk(bookshelf_id)
        REFERENCES Bookshelf (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS Language(
    id INT AUTO_INCREMENT PRIMARY KEY,
    language VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS BookLanguage(   -- tabella per collegare Book con Language
    book_id INT,
    language_id INT,
    PRIMARY KEY(book_id,language_id),
        CONSTRAINT Constr_BookLanguage_Book_fk
            FOREIGN KEY Book_BookLanguage_fk(book_id)
            REFERENCES Book (id) ON DELETE CASCADE ON UPDATE CASCADE,
        CONSTRAINT Constr_BookLanguage_Language_fk
            FOREIGN KEY Language_BookLanguage_fk(language_id)
            REFERENCES Language (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS Format(
    id INT AUTO_INCREMENT PRIMARY KEY,
    textHtml VARCHAR(255),
    applicationEpubZip VARCHAR(255),
    applicationMobiPocket VARCHAR(255),
    textPlainAscii VARCHAR(255),
    textPlainUtf8 VARCHAR(255),
    textHtmlCharsetUtf8 VARCHAR(255),
    applicationRdfXml VARCHAR(255),
    imageJpeg VARCHAR(255),
    applicationOctetStream VARCHAR(255),
    downloadCount INT NOT NULL,
    book_id INT,
    CONSTRAINT fk_Book_Format FOREIGN KEY (book_id) REFERENCES Book(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS Review(
    id INT AUTO_INCREMENT PRIMARY KEY,
    bookId INT NOT NULL,
    review VARCHAR(255) NOT NULL,
    score INT NOT NULL,
    status VARCHAR(50),
    book_id INT,
    CONSTRAINT fk_Book_Review FOREIGN KEY (book_id) REFERENCES Book(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS Editor(
    id INT AUTO_INCREMENT PRIMARY KEY,
    editor VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS BookEditor(   -- tabella per collegare Book con Editor
    book_id INT,
    editor_id INT,
    PRIMARY KEY(book_id,editor_id),
        CONSTRAINT Constr_BookEditor_Book_fk
            FOREIGN KEY Book_BookEditor_fk(book_id)
            REFERENCES Book (id) ON DELETE CASCADE ON UPDATE CASCADE,
        CONSTRAINT Constr_BookEditor_Editor_fk
            FOREIGN KEY Editor_BookEditor_fk(editor_id)
            REFERENCES Editor (id) ON DELETE CASCADE ON UPDATE CASCADE
);