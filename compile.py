#! /usr/bin/python

__author__="Vasek Vopenka"
__date__ ="$21.4.2010 17:28:04$"

from nis import cat
from commands import getoutput;
import os;
import sys;
import shutil;
import string;

tmpfolder = "mytmpfolderdsfs";

def jar_file(fname):
    return string.rfind(fname, ".jar") != -1;

if __name__ == "__main__":
    files = os.listdir('.');
    if tmpfolder in files:
        print >> sys.stderr, 'Temporary dir you want to use already exists.';
        exit(0);
    if (len(files) == 0) or not("dist" in files):
        print >> sys.stderr, 'Seem to be in a wrong directory';
        exit(0);
    files = os.listdir('dist');
    mainfile = filter(jar_file, files);
    if len(mainfile) == 0:
        print >> sys.stderr, 'jar file in dist directory not found';
        exit(0);
    mainfile = mainfile[0];
    if not("lib" in files):
        shutil.copy(mainfile, ".");
    else:
        os.mkdir(tmpfolder);
        files = os.listdir('dist/lib');
        for file in files:
            getoutput('unzip -d '+tmpfolder+' dist/lib/'+file);
            shutil.rmtree(tmpfolder+'/META-INF');
        getoutput('unzip -d '+tmpfolder+' dist/'+mainfile);
        os.chdir(tmpfolder);
        fread = open('META-INF/MANIFEST.MF', 'r');
        fwrite = open('META-INF/MANIFEST_new.MF', 'w');
        for line in fread:
            if line.split(':')[0] == 'Class-Path':
                break;
            else:
                fwrite.write(line);
        fwrite.write("\n");
        fread.close();
        fwrite.close();
        os.remove('META-INF/MANIFEST.MF');
        shutil.move('META-INF/MANIFEST_new.MF', 'META-INF/MANIFEST.MF');
        getoutput('zip -r '+mainfile+' *')
        try:
            shutil.move(mainfile, '../');
        except shutil.Error:
            os.remove('../'+mainfile);
            shutil.move(mainfile, '../');
        os.chdir('..');
        shutil.rmtree(tmpfolder);