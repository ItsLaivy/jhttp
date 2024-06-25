package codes.laivy.jhttp.element;

/**
 * The {@code Method} enum represents the various HTTP methods that can be used in a request.
 * Each constant corresponds to a standard HTTP method, defining the type of action that the
 * request intends to perform.
 * This enum is essential for handling and dispatching HTTP requests
 * correctly in web applications, ensuring that the appropriate actions are taken based on the
 * request type.
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0-SNAPSHOT
 */
public enum Method {

    /**
     * The HTTP POST method is used to submit data to be processed to a specified resource.
     * It is typically used when submitting form data or uploading a file.
     * The data is included
     * in the body of the request.
     * POST-requests can result in the creation of new resources
     * or the update of existing resources.
     *
     * @since 1.0-SNAPSHOT
     */
    POST,

    /**
     * The HTTP DELETE method is used to delete a specified resource.
     * This method removes the
     * resource identified by the URI.
     * Upon successful deletion, the server typically returns
     * a 200 (OK) or 204 (No Content) status code.
     *
     * @since 1.0-SNAPSHOT
     */
    DELETE,

    /**
     * The HTTP GET method is used to request data from a specified resource. It is one of the
     * most common methods used in web development for retrieving data. GET requests should not
     * modify any resources and should be idempotent, meaning multiple identical GET requests
     * should have the same effect as a single request.
     *
     * @since 1.0-SNAPSHOT
     */
    GET,

    /**
     * The HTTP HEAD method is similar to the GET method, but it only requests the headers of a
     * specified resource, not the body. It is useful for checking what a GET request will return
     * before actually making the GET request, or for testing if a resource exists.
     *
     * @since 1.0-SNAPSHOT
     */
    HEAD,

    /**
     * The HTTP PUT method is used to upload a representation of the specified resource. If the
     * resource does not exist, it can be created. If it does exist, it is replaced. PUT requests
     * are important, meaning that repeated requests will produce the same result.
     *
     * @since 1.0-SNAPSHOT
     */
    PUT,

    /**
     * The HTTP TRACE method performs a message loop-back test along the path to the target resource.
     * It is mainly used for diagnostic purposes, to see what changes or additions are made to a
     * request by intermediate servers.
     *
     * @since 1.0-SNAPSHOT
     */
    TRACE,

    /**
     * The client uses the HTTP CONNECT method to establish a network connection to a web server
     * over a specific HTTP proxy server.
     * It can be used to create a tunnel for communication through
     * an intermediary.
     *
     * @since 1.0-SNAPSHOT
     */
    CONNECT,

    /**
     * The HTTP OPTIONS method is used to describe the communication options for the target resource.
     * It allows a client to determine the options and/or requirements associated with a resource,
     * or the capabilities of a server, without implying a resource action.
     *
     * @since 1.0-SNAPSHOT
     */
    OPTIONS,

    /**
     * The HTTP PATCH method is used to apply partial modifications to a resource. It is similar to
     * the PUT method, but unlike PUT, PATCH is not important. It provides a way to update resources
     * without sending the entire resource data, only the changes.
     *
     * @since 1.0-SNAPSHOT
     */
    PATCH,
    ;

}
