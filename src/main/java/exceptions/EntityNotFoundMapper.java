package exceptions;

import resources.APIResponseUtils;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class EntityNotFoundMapper implements ExceptionMapper<NotFoundException> {

    public Response toResponse(NotFoundException ex) {
        return APIResponseUtils.notFound().build();
    }

}