package nabu;

import javax.jws.WebService;

import be.nabu.eai.api.Eager;
import be.nabu.eai.api.Hidden;

@WebService
public class Scheduler {
	
	@Eager
	@Hidden
	public void initialize() {
		// TODO: start a scheduler thread
	}
	
	// TODO: add some methods to check which scheduler is running, to activate/deactivate a scheduler etc
}
