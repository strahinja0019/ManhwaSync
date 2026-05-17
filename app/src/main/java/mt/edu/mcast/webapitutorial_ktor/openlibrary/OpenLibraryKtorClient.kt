package mt.edu.mcast.webapitutorial_ktor.openlibrary

import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// Top-level val — initialized once, shared across the app.
val ktorClientOL = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true // Handle null/missing values better
        })
    }
    engine {
        maxConnectionsCount = 100
        endpoint {
            maxConnectionsPerRoute = 20
            pipelineMaxSize = 20
            keepAliveTime = 5000
            connectTimeout = 5000
            connectAttempts = 5
        }
    }
}
