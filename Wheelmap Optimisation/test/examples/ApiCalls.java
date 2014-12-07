package examples;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.athmis.wmoptimisation.changeset.ChangeSetToolkit;
import org.athmis.wmoptimisation.fetch_changesets.FetchingChangeSetsToolbox;

public class ApiCalls {

	public static void main(String[] args) {
		String apiCall;
		Instant closed, created;
		String createdT2, closedT1;

		// note: http://blog.progs.be/542/date-to-java-time
		LocalDate lc = LocalDate.of(2014, 1, 1);
		closed = Instant.from(lc.atStartOfDay().atZone(ZoneId.of("GMT+2")));

		lc = LocalDate.of(2014, 6, 1);
		created = Instant.from(lc.atStartOfDay().atZone(ZoneId.of("GMT+2")));

		createdT2 = ChangeSetToolkit.OSM_DATE_TO_JAVA.format(Date.from(created));
		closedT1 = ChangeSetToolkit.OSM_DATE_TO_JAVA.format(Date.from(closed));

		apiCall =
			String.format(	FetchingChangeSetsToolbox.getApiCallForPeriod(false), "roald-linus",
							closedT1, createdT2);

		System.out.println(apiCall);

	}
}
