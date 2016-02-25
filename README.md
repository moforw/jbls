# jbls

## What
jbls is a simple, fast, encrypted, local storage database library written in 
Java.

## Why
The future of software is distributed, that much should be clear by now. 
Encryption helps no one if all it takes is a take down notice or government 
sponsored DDOS attack. That means I'd rather put my eggs in the simple, fast,
encrypted, local storage basket than go mad turning knobs on hairy solutions to
the wrong problem.

## Columns
Each table has a set of columns. Four are predefined in Tbl: Id, InsTime, 
Rev & UpTime. Each predefined column has corresponding predefined methods in 
the Rec interface and a field in BasicRec.

### Readers & Writers
Calling Col.reader(), and/or Col.writer() is optional. That is, you can leave 
them empty if you don't need them. One use of that possibility is to leave
out the writer for final record fields; which let's you ensure that they are
only initialized, never overwritten. Leaving both unimplemented gives you a 
transient column; one that can be indexed and used like any other, but never 
actually interacts with storage.

## Records
All record types must implement the Rec interface. The BasicRec class contains 
a default implementation for extending.

## Transactions
jbls uses transactions mainly as a way to batch multiple operations and
execute them as an atomic unit. Last commit wins, both in multi thread and 
multi process scenarios. The Trans class is a simple Closeable wrapper that 
rolls back any left over changes when closed. Transactions are usually scoped 
with try () to perform  clean up in case of errors. New transactions can be 
created by calling DB::trans().