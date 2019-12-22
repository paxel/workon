# work on
**work on** is a command line tool that manages your current activities and interruptions.

# overview
work on is a tool, that is initially written in java (because that's the language I know best) and will be reimplemented in rust asap.
The tool stores the activities in a file in `~/.workon/activities` and provides as cli the ability to manipulate and show the content.


# usage
## start [ID [Description]]
**without parameter** , the last unfinished activity is continued

**~$ wo start**  
`It's 08:15 and your last activity was '7 - Finish the presentation'`  
`You are working on 'Finish the presentation'.`  

**with only an ID given** , the activity with that ID is started (if it exists)

**~$ wo start 7**  
`It's 08:15 and you are working on 'Finish the presentation'`  

or an error is printed

**~$ wo start 8**  
`There is no activity with ID '8'`  

**with both ID and Description given**, the old task with the same ID will be finished and a new task with the ID is started

**~$ wo start 7 Review student task**  
`It's 08:15 and you have finished working on 'Finish the presentation' and started working on 'Review student task'`  

if there was no task with that ID, no previous task is finished.

**~$ wo start 8 Review student task**  
`It's 08:15 and you started working on 'Review student task'`  


## status
status is also the default command if none is given.

**~$ wo status**  
`Today you started at 08:15 and have been active for 3h and 25m. you have been idle for 30m`  
`Activities:`  
`08:15 - 09:30 Finish the presentation  (1h 15m)`  
`09:30 - 10:00 Review student task      (0h 30m)`  
`10:00 - 10:15 standup                  (0h 15m)`  
`10:15 - 11:40 Review student task      (1h 25m)`  

`You are currently idle`  

## stop
if there is a current activity, its end is marked, and the status is set to idle.

**~$ wo stop**  
`You finished working on 'Review student task'`  
`You are currently idle`  

## switch [ID]
This switches between activities without creating new ones. Without parameter the last activity becomes the current one.

**~$ wo switch**  
`It's 08:15 and you started working on 'Review student task'`  
**~$ wo switch**  
`It's 08:16 and you started working on 'Finish the presentation'`  
**~$ wo switch**  
`It's 09:15 and you started working on 'Review student task'`  







