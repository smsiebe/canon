canon
=====

Event Stream Framework

Canon is a specialized distributed sequential event store.  

**Program Background**

   Canon is a persistent sequential event store, ensuring events are read 
(streamed) in the squence which they were successfully committed - simply put 
Canon may be thought of as a write-ahead log. 

   How Canon is used is application specific, though Canon was designed to be used 
as the single authoritative source for all changes within a domain, providing 
not only details about any state change, but the context in which that state 
was changed.  Used in this manner Canon ensures data integrity, allowing downstream 
applications to focus on a specific business problem by handling the events as 
they arrive, potentially storing event content or derived state in a store ideal  
for the applications retrieval needs. Applications changing domain state would, instead 
of writing to its local store, publish events to canon, which then in turn is handled by 
the application event handlers.  This architectural design results in significant 
benefits in terms of security, performance, development, and maintenance.  

   Canon may be used as an embedded database, deployed as a network service, or used 
in combination to support online/offline operations.

   Though canon may be used as the primary data store for traditional applications 
architectures, the power of canon comes from the way it facilitates an 
alternative approach to implementing data persistence and retrieval within 
an application.  


**Canon and Terpene**

   Canon is the backbone of the [terpene](https://terpene.geoint.org)
platform, providing a communication and data persistence framework to realize 
the data confidentiality, complete non-repudiation, and availability 
requirements of terpene.  

