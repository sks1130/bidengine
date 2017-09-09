# bidding engine platform is implemented assuming that this is a core engine and above that load balancers/nginx on application server and Databases are already done.
#Here it is recommended to use distributed cache using couchBase or Memcached or Redis for the faster retrival
#core engine is possible to scale horizontally  if higher loads on the DBs occur then we should follow the Sharding on the collection/table of bidOrder.
#its a maven based project , generate also an executable jar with the option of filename as input or blank if want to test on the console.# bidengine
