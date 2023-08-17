-- // Date of Listing oldest to newest
SELECT dateofbirth FROM tblpatient_info ORDER BY dateofbirth;

-- Date of Listing newest to oldest 
SELECT dateofbirth FROM tblpatient_info ORDER BY dateofbirth DESC;

-- // By Product 
-- This field is choosen because this field has duplicate entry in one table
SELECT DISTINCT attending_physician_hw FROM tbltransrecord;

-- // By Location 
-- This field is choosen because this field has duplicate entry in one table
SELECT DISTINCT attending_physician_hw FROM tbltransrecord;

-- // Lowest to Highest Price 
-- The cityid can be replace by price
-- The DISTINCT KEYWORD should be !!!REMOVED if youre going to use 
SELECT DISTINCT cityid FROM tblpatient_info ORDER BY cityid;

-- // Highest to Lowest Price
-- The cityid can be replace by price
-- The DISTINCT KEYWORD should be !!!REMOVED if youre going to use 
SELECT DISTINCT cityid FROM tblpatient_info ORDER BY cityid DESC;

-- // Price Range
SELECT cityid FROM tblpatient_info WHERE cityid BETWEEN 500 AND 1000;