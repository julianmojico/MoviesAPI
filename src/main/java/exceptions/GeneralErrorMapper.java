package exceptions;

import resources.APIResponseUtils;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class GeneralErrorMapper implements ExceptionMapper<Exception> {

    public Response toResponse(Exception ex) {
        return APIResponseUtils.serverError(ex.getMessage()).build();
    }
}
