import git.sync.exception.FileVerificationException;
import git.sync.exception.ProjectRevisionException;
import git.sync.updaters.GitUpdaterImpl;

import java.io.IOException;

/**
 * Created by Unknown on 5/01/2016.
 */
public class TestUpdater {
    public static void main(String[] args) {
        GitUpdaterImpl gitUpdater = new GitUpdaterImpl();
        try {
            gitUpdater.tryUpdate();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProjectRevisionException e) {
            e.printStackTrace();
        } catch (FileVerificationException e) {
            e.printStackTrace();
        }
    }
}
