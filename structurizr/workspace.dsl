workspace "MovieManager" "This is a project to show howto manage your movies with search extended to full text with an Angular Frontend and a Spring Boot Backend" {

    model {
        user = person "User"
        movieManagerSystem = softwareSystem "MovieManager System" "MovieManager System" {
        	kafka = container "Kafka Event System(Optional)" "Kafka provides the events between multiple deployed MovieManager applications."
        	movieManager = container "MovieManager" "Multiple instances possible. Angular Frontend and Spring Boot Backend integrated." {
        		angularFrontend = component "Angular Frontend" "The SPA searches/shows the movies / actors." tag "Browser"
        		backendMovieClient = component "Rest Client" "The rest client to import the movie data."
        		backendJwtTokenFilters = component "Jwt Token Filters" "Provide the security based on Jwt Tokens."
        		backendMovieActorControllers = component "Movie/Actor Controllers" "Provides the rest interfaces for Movies / Actors."
        		backendAuthController = component "Auth Controller" "Provides the rest interfaces for Login / Signin / Logout."
        		backendImportController = component "Import Controller" "Provides the rest interfaces to start movie related imports."
        		backendKafkaConsumer = component "Kafka Consumer" "Consume the Kafka events." tag "Consumer"
        	    backendKafkaProducer = component "Kafka Producer" "Produce the Kafka events." tag "Consumer"
        	    backendMovieRelatedRepositories = component "Movie related Repositories" "Repositories for Cast / Actor / Genere / Movie."
        	    backendUserTokenRepository = component "User/Token Repositories" "User / Token Repositories"        	    
        	    backendMovieRelatedServices = component "Movie related Services" "Services for Cast / Actor / Genere / Movie."
        	    backendUserService = component "User Service" "User Service"        	    
        	}
        	database = container "Postgresql Db" "Postgresql stores all the data of the system." tag "Database"
        }
		theMovieDbSystem = softwareSystem "The MovieDb System" "The Movie Database Rest interface"
		
		# relationships people / software systems
        user -> movieManagerSystem "manages movies"
        movieManagerSystem -> theMovieDbSystem "imports movie data"
        
        # relationships containers
        user -> movieManager "manages movies"
        movieManager -> kafka
        kafka -> movieManager
        movieManager -> database
        movieManager -> theMovieDbSystem "imports movie data"
        
        # relationships components
        angularFrontend -> backendMovieActorControllers "rest requests"
        angularFrontend -> backendAuthController "rest requests"
        angularFrontend -> backendImportController "rest requests"
        backendMovieActorControllers -> backendJwtTokenFilters
        backendAuthController -> backendJwtTokenFilters
        backendImportController -> backendJwtTokenFilters
        backendMovieActorControllers -> backendMovieRelatedServices
        backendAuthController -> backendUserService
        backendImportController -> backendMovieRelatedServices        
        backendMovieRelatedServices -> backendMovieClient "import movie related data"
        backendKafkaConsumer -> backendUserService "process kafka events"
        backendUserService -> backendKafkaProducer "send kafka events"
        backendMovieRelatedServices -> backendMovieRelatedRepositories
        backendUserService -> backendUserTokenRepository
    }

    views {
        systemContext movieManagerSystem "SystemContext" {
            include *
            autoLayout
        }
        
        container movieManagerSystem "Containers" {
        	include *
            autoLayout lr
        }
        
        component movieManager "Components" {
        	include *
            autoLayout
        }    
        
        styles {
        	element "Person" {            
            	shape Person
        	}
        	element "Database" {
                shape Cylinder                
            }
            element "Browser" {
                shape WebBrowser
            }
            element "Consumer" {
            	shape Pipe
            }
        }
    }

}