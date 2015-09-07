# Expert-Identification-Service
This is a RESTful service developed with las2peer a java-based server framework for developing and deploying services in a distributed Peer-to-Peer (P2P) environment. This services finds experts in Question-Answer forum like data structures. Experts are people with high level of knowledge which can contribute to novices. In Learning Layers project, social semantic server would require such a service for discovering the people with higher level of expertise.

This framework provides access to various algorithms such as modeling and graph based algorithms that can be applied on the dataset. This framework provides sanitization and indexing of the data out of the box. This framework works best with the dataset which are in the form of question and answers such as www.stackexchange.com . This can also be extended to other form of dataset easily. This primarily uses LAS2peer server that was developed by the Advanced Community Information Systems (ACIS) group at the Chair of Computer Science 5 (Information Systems & Databases), RWTH Aachen University, Germany.

## Using Framework
This is developed using [LAS2Peer](http://las2peer.com) and hence pre knowledge of LAS2Peer is necessary if you want to understand the framework. Framework provides multiple RESTful endpoints that has to be used for the proper execution of the framework pipeline.

Follow the steps below to successfully use the service to recommend experts.

* Dataset is uploaded to the server. This is done either manually by copying the desired dataset to the server or configuring the url end point to automatically parse the dataset from the remote url.
* Data is parsed, sanitized and inserted into the MySql database. This includes mainly posts and user details.
* Inserted data in the database is then indexed using lucene indexer.
* User of the service can then send queries to the framework by requesting the specific algorithm to be executed on the dataset for the recommendation.

### Preperations
* Make sure that MySql is installed and run the sql script provided in the framework. This will install the db called ersdb. This db holds the configuration information such as dataset directory, indexing directory and dataset name. Update these information accordingly.
* Make sure that all the libraries are installed. Check the build instructions for it.

### Supported types
* XML
* JSON
* CSV


### Supported Algorithms
* Pagerank
* HITS
* Community Aware Pagerank
* Community Aware HITS
* Modeling technique

## Building Instructions
* Run "ant" from the root directory of the service. This should install all the necessary libraries required by the service.

## Extending to external dataset
* If the dataset is in the expected format(Check the data format section for it), user of the service do not have to configure anything.
* Configure the url from which to fetch the data in the ers.configuration file.
* If the dataset has different fields than expected by the framework. Map the fields of the posts and the user in the file config/data_mapping.xml and config/user_mapping.xml

Above configuration should be enough to start using the features of the framework.

## Data format.
There are two important files that framework looks for to parse and save the data into database. This should follow the format mentioned below.
* Data format

	```xml
		<?xml version="1.0" encoding="utf-8"?>
			<posts>
			  <row Id="124" PostTypeId="1" CreationDate="2011-03-01T19:49:22.47"
                   Body="body of the post" OwnerUserId="20" Title="Title of the post" />
		    </posts>
	```
* User data format

	```xml
		<?xml version="1.0" encoding="utf-8"?>
		<users>
		  <row Id="123" Reputation="1" CreationDate="2011-03-01T01:15:13.14" DisplayName="Alex" LastAccessDate="2011-03-01T01:15:13.147" WebsiteUrl="http://xyz.com" Location="Aachen" AboutMe="Details about the user" AccountId="-1" />
          </users>
	```


## RESTful endpoint.
	Check the swagger documentation for all the available end points.






