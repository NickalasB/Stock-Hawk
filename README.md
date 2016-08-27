Quick summary: Hot Movies is an app that allows you to discover the most popular and top rated movies out there using data from TheMovieDB.org

Version: This is the first iteration of the app- more features will be added

How do I get set up?
To get this app up and running:
Open up Android Studio and simply select "Import Project"
Once you have the app imported you will need to obtain your an API Key to access the movie data
You can get a MovieDB API key from themoviedb.org
Once you've obtained an API Key add it to you "build.gradle" file in your "Gradle Scripts" class like so
    buildTypes.each {
        it.buildConfigField 'String', 'MOVIE_DB_API_KEY', "\"Your API Key here\""

    }
}
Once you've got that configured choose Run 'app' from the Run dropdown menu.
Navigating the App
When you launch the app you will be presented with a GridView of movie posters.
You can choose to view either the most popular movies, the highest rated movies, or a list of favorite movies by selecting your preferred option in the options menu in the upper right-hand corner.
Click on a movie that interests you and you'll be taken to a new activity or, if your're on a tablet, see a new display that will give you some key info about your selected movie:
Title
Release date
Average rating
Total ratings
Movie Summary
Movie trailers
Movie reviews
Pretty cool!
More features will be added. This app is being built as part of Udacity's Android Nanodegree program