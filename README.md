# opsi-barcode
Helper for App "OPSI-Admin" displaying a 2D-Code which points to the current client

Purpose: Assume you stand in front of a PC controlled by your OPSI host (see http://uib.de). 
You already use the OPSI Admin App (see Google Playstore), and you would like to control it right now.

What you don't know is which PC you are at. What if it could display its OPSI Id (the hn= property) 
in the format {"dns":"<current hn>"}  right for the scanner in your Android?

This is what this tiny .jar is made for.

Building needs the org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean and some helpers to display the result.
