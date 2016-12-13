# Clustered Scheduling

There are two ways for clustered schedulers to work:

## Database Controlled

Each server in the cluster fires the scheduler, then tries to get a lock in a centralized database for the privilege to run the scheduler.

Note that this means each scheduler "fire" has a unique id, especially for "simple" schedulers this can be somewhat tricky if you don't have a fixed starting point. Suppose server 1 started at "12.00.01" and should fire ever 10 seconds, it will fire at "12.00.11". Server 2 meanwhile started at "12.00.02" and will fire at "12.00.12". Unless they can agree upon a starting time to start firing for a scheduler, they can not get their schedules aligned.

A starting time can be tricky to get however, do you simply have the user configure one? This means depending on the age of the configuration, it might take quite a bit of calculation to get to the "next run". Suppose the configuration is 2 years old and you fire every 10 seconds, you need to compute 6307200 (2*365*24*60*6) schedules to get to the next one. 

You can not use a "relative" fixed point like "start of the week" because any relative point can lead to out-of-sync schedulers if servers run long enough and schedules don't exactly line up with the interval of your fixed point (e.g. a week).

Alternatively you can use the previous record in the database as a starting point. This only leaves you some initial locking logic to get the first entry in there.

The downside to this approach is that you _need_ a database which can become the bottleneck of your scheduling.

The upside of this approach is that your servers don't need to know about each other.

Additional note: you can use an agreed upon starting point dictated by the master for example it's starting time.

## Master Controlled

Only the master server fires the scheduler and chooses (round robin, load based,...) which peer will execute it (or he himself might).

The advantage of this approach is that you don't need an external component like a database. Your master server does become the puppet master so he can become a bottleneck in larger schemes.

The downside of this approach is that you can not guarantee a simple scheduler's schedule. Because there is no guaranteed permanent record, the simple scheduler in the above example will always start 10 seconds after the master servers start time. If you switch masters, that one will dictate the schedule.

However in my experience for heavy tasks that should run at fixed intervals, you generally use a complex scheduler which has a fixed schedule that is independent of the start time of the server. Lighter tasks meant for polling systems use simple schedulers where a guaranteed timeframe is not generally mandatory.