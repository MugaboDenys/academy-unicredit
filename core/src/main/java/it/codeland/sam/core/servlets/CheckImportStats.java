package it.codeland.sam.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;

import com.google.gson.Gson;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {Servlet.class})
@SlingServletPaths(value = {"/bin/servlets/importjobcheck2"})
@ServiceDescription("Import CSV file and creates article pages")
public class CheckImportStats extends SlingAllMethodsServlet {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Reference
    private JobManager jobManager;

    private class JobData{
        String state;
        String progressStep;
        String processLogOk;
        String processLogKo  ;
        String processLogSkipped ;
    }
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response){
        String requestData;
        try {
            requestData = request.getReader().readLine();
            JSONObject json = new JSONObject(requestData);
            String id = json.getString("id");
            JobData jobData = new JobData();
            Job job = jobManager.getJobById(id);
            if(job != null){
                jobData.state = job.getJobState().toString();
                int step =  Integer.parseInt(job.getProperty(Job.PROPERTY_JOB_PROGRESS_STEP, String.class) != null? job.getProperty(Job.PROPERTY_JOB_PROGRESS_STEP, String.class): "0");
                int steps =  Integer.parseInt(job.getProperty(Job.PROPERTY_JOB_PROGRESS_STEPS, String.class) != null? job.getProperty(Job.PROPERTY_JOB_PROGRESS_STEPS, String.class): "0");
                double result;
                if(steps !=  0){
                    result = ((double) step / (double) steps);
                    result = result * 100;
                } else{
                    result = 0;
                }
                jobData.progressStep = String.format("%1$,.2f", result) + "%";

                String[] tmp = job.getProgressLog();
                if((tmp != null) && (tmp.length > 0)){
                    // logger.debug("BBBBBBBBBBB {}", jobData.progressStep + " | "+ tmp);
                    jobData.processLogOk = job.getProgressLog()[0];
                    jobData.processLogSkipped = job.getProgressLog()[1];
                    jobData.processLogKo = job.getProgressLog()[2];
                }
            }
            String answer = new Gson().toJson(jobData);
        logger.info("NNNNNNN {}", answer);

            response.setContentType("application/json");
            try {
                response.getWriter().println(answer);
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        } catch (IOException | JSONException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
