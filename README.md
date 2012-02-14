# Talos

## Access

This is the main repository for the SDP team.  Before all team members receive 
pull & push access we need to go through how to use github correctly and 
effectively.

### Basic commands

To set up a local git repository:

	git clone git@github.com:DavidAndTheArgonauts/Talos.git

Get status of repository and staged files:

	git status

Add file to staging area (to be commited):

	git add file-name

Commit files (with a commit message):

	git commit -m "message"

List all branches:

	git branch -a

Creating a new local branch:

	git checkout -b branch-name

Adding the branch to github:

	git push origin branch-name

Deleting a local branch (there is no going back):

	git branch -D branch-name
	
Deleting a branch on github:

	git push origin :branch-name

Change branch:

	git checkout branch-name

Pulling:

	git pull
	
Pushing:

	git push

Merging (merges branch-name into current code):

	git merge branch-name

List all commits:

	git log

### Basic Working Practices

#### Making Changes

1. Create a new branch
2. Push the branch to github
3. Change to the new branch
4. Make changes to the file(s)
5. After each change add and then commit the file(s)
6. Pull the latest verion
7. Resolve any conflicts (see next section)
8. Push the latest version

#### Merging Changes

1. Change to the branch with the changes
2. Pull the latest version
3. Resolve any conflicts
4. Change to the branch you want to merge into
5. Merge the two branches
6. _(Optional)_ Delete the branch locally and from github

#### Conflict Resolution

When presented with a conflict which git cannot resolve you must do it manually.
Git will present you with a list of files which contain conflicts.  Resolving 
them is a straight forward process outlined in the steps below:

1. Open each file with conflict(s)
2. Find the conflicts and change it to the version you want to commit
3. After making all changes, add the files and commit

### Getting access

To get full access please speak to me (Will) in the labs or give me an email at
w.pond@sms.ed.ac.uk.

## Branches

Currently there are the following branches:

* master - _no one should push to this branch_
* milestone - _this contains working milestone code_
* develop - _when making changes see basic working practices - making changes_

The three teams are welcome to make any other branches they need.

### Folders

There are 3 main folders available - robot, strategy and vision.  When working 
as part of a team make sure to only edit the files relating to that team.
