# Description for Status Codes

## 2000 Successful
**2000 OK**
Request has succeeded.

## 4000 Client Error
**4000 Unsupported Format**
Request is in a format not supported by the requested resource for the requested method.

**4010 Authentication Failure**
Authentication failed due to inaccurate credentials input by user.

**4011 Unauthorized**
Requested resource requires user authentication. The request has either been excluded authorization credentials or been included a rejected credential. Client MAY repeat the request with a suitable Authorization header field.

**4030 Forbidden**
Request is not make public. Client should not repeat the request.

**4040 Resource Not Found**
The requested resource is not found.

## 5000 Server Error
**5000 Internal Server Error**
The server encountered an unexpected condition in fulfilling the request.

**5001 Duplicated Entry**
The database encountered an unexpected condition in fulfilling the request.
