begin;
 /* Anpassung für ghostscript ab Version */
 /* -dUseCIEColor wird nicht mehr unterstützt stattdessen -sColorConversionStrategie=CMYK */
update conversion_routines set params = 'gs -q -dPDFA -dPDFACompatibilityPolicy=1 -dBATCH -dNOPAUSE -dNOOUTERSAVE -sColorConversionStrategie=CMYK -sProcessColorModel=DeviceCMYK -sDEVICE=pdfwrite -sOutputFile=output conf/PDFA_def.ps input' where id = 6;
commit;

