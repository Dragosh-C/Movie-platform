# Project OOP - POO TV

## Project Description
The project consists of implementing a simple backend of a specific platform
for viewing movies and series.

# Part 1

## Implementation
First, I created a class to load the data from the test file into objects,
after that I have implemented Output class for displaying the result of the actions
in the output JSON file.

To perform the actions, I created 2 main classes: ChangePage and OnPage. They contain
the execute method that will be called in the DoActions class. In this class, I went
through the list of actions and depending on their type, I called the appropriate method.

To know what page I am on, I created a global variable(currentPage) in the Singleton class.
I used this pattern to provide a global point access to its variables.

### ChangePage Class
In this class I performed actions of the type: "change page", and checked if it is 
possible to access the given page, and if not, I displayed the corresponding error.

### OnPage Class
This class is the most complex, in it, I created a separate method for each action.
Because we have several types of sorting, I used the strategy pattern to sort 
ascending/descending or to sort according to duration or rating.


# Part 2
For the second part of the project I first added notification field in the list, and
also created a new notification parameter in User.
## Subscribe
For this action I added a new method in OnPage class. First I iterated through the 
list of subscribed genres and verified if that genre already exist, after that I 
added new genres in the list.

## Database add (in DoAction class)
First I verified if movie already exist in the list, if exist I wrote standard error
message. If not, I added movie to the list. Then I checked if the current user country
is not in the banned list and added movie to the user. After that I added notification
message with the same restrictions to user.

## Database delete
I iterated through movies and current user database and deleted the movie specified in
actions. For that I used listIterator.

## Back action
For this action I used a list where I put all the pages I navigated through when the 
user was logged in. When the back action was performed, I took the last element from 
the list and passed it to the action object as a change page type action. And then 
deleted that element from the list.

## Premium user recommendation
First I created a treeMap to store genres in alphabetical order. After that I sorted 
the Map and the current user movies decreasingly by number of likes. And then I took
each movie that contains the genre appreciated by the user and if the movie is not 
viewed, I add it to the notifications.


