package exceptions;

import resources.APIResponseUtils;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class MethodNotAllowedMapper implements ExceptionMapper<NotAllowedException> {

    public Response toResponse(NotAllowedException ex) {
        return APIResponseUtils.methodNotAllowed().build();
    }
}
