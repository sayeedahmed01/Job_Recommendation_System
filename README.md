# JobScraper: The Automatic Job Posting Recommender

Team Members:

    Chethan U Mahindrakar (002646783)
    Sayeed Ahmed (002191535)


## Problem Statement and Idea
### Problem Statement:

Job seekers often find it challenging to efficiently track job postings on Indeed. Manually checking for new job listings every day is not only time-consuming but can also lead to missed opportunities.

### Solution:

Our solution is to develop a web scraping application called JobScraper, which automatically retrieves job postings from Indeed, processes the data, and stores it in a database. We have created a API that allows users to interact with the database and filter job postings based on their resume.

Additionally, we will develop a user-friendly Scala.js web interface that enables job seekers to search for job postings relevant to their skills and experience. This will make it easier for job seekers to discover suitable opportunities and stay up-to-date with the latest openings in their desired field.

## Architecture
![architecture.png](jobscapper%2Fsrc%2Fmain%2Fresources%2Farchitecture.png)

## Installation
1. Clone the repository
2. Setup the MySQL server (Database dump can be found at `src/main/resources/Database`) and update the database credentials in `src/main/scala/JobDataProcessor.scala`
3. Run the MainApp.scala file to start the web scraping process
//need review
4. Run the RMCheckerApplication.scala file to start the API server
5. Open the Scala.js web interface by running `sbt fastOptJS` within the directory and opening and then starting a local server using the `http-server` command.
6. Upload your resume and select the desired job category to view relevant job postings
7. The results will be displayed in the Scala.js web interface

## Usage

### Use Case: Job seeker looking for new opportunities

Upload your resume in the Scala.js web interface and select the desired job category.
The application will display a list of job postings that match your skills and interests, allowing you to easily find relevant job openings.

