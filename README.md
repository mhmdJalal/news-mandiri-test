# news-apps

A mobile native application to show news using API from [newsapi.org](https://newsapi.org)

## User stories
1. Create a screen to display the list of news categories.
2. Show news sources when user click one of the news category.
3. Show news articles when user click one of the news source.
4. Show the article detail on web view when user click one of the article.
5. Provide function to search news sources and news articles.
6. Implement endless scrolling on news sources and articles screen.
7. Cover positive and negative cases.

## Tech stack & third party libraries
- Minimum SDK level 21
- [Kotlin](https://kotlinlang.org/) based, [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) + [State Flow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/) for asynchronous.
- [Koin](https://insert-koin.io/) as dependency injection.
- Jetpack
   - Lifecycle: Observe Android lifecycles and handle UI states upon the lifecycle changes.
   - ViewModel: Manages UI-related data holder and lifecycle aware. Allows data to survive configuration changes such as screen rotations.
   - ViewBinding: Feature that allows you to more easily write code that interacts with views.
- Architecture
   - MVVM Architecture
   - Repository Pattern
- [Ktor](https://ktor.io): Handle everything related to the network. e.g. Construct the REST APIs, interceptor, logging, etc.
- [Detekt](https://detekt.dev): Static code analyzer for better clean code.
- [Glide](https://github.com/bumptech/glide): Loading images from network.
- [Splitties](https://github.com/LouisCAD/Splitties): Intended to reduce the amount of code you have to write, freeing code reading and writing time.
- [prettytime](https://github.com/ocpsoft/prettytime): Social Style Date and Time Formatting.

## Running the Application
1. Clone this project.
2. Setting up the API Key
   To be able to gather article info from the News API you will need an API Key.

    * Visit https://newsapi.org/ and register for an API key.
    * In the root directory of the project folder create a file: apikey.properties
    * Insert the following info into it:

            NEWS_API_KEY=YOUR-API-KEY

    * Insert the API Key you received from News Api where YOUR-API-KEY is.
3. Sync the gradle files.
4. Run the project.