--
-- Table structure for table `datasetInfo`
--

CREATE TABLE IF NOT EXISTS `datasetInfo` (
`Id` int(11) NOT NULL,
  `dataset_name` text NOT NULL,
  `index_path` text NOT NULL,
  `created_on` text NOT NULL,
  `database_name` text NOT NULL,
  `original_filepath` text NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

--
-- Indexes for table `datasetInfo`
--
ALTER TABLE `datasetInfo`
 ADD PRIMARY KEY (`Id`);


--
-- AUTO_INCREMENT for table `datasetInfo`
--
ALTER TABLE `datasetInfo`
MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=11;