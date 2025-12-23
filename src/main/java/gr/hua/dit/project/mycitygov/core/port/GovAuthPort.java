package gr.hua.dit.project.mycitygov.core.port;

import gr.hua.dit.project.mycitygov.core.port.exception.GovAuthException;
import gr.hua.dit.project.mycitygov.core.port.model.GovLoginResult;

public interface GovAuthPort {
   GovLoginResult login(String afm, String pin) throws GovAuthException;
}
