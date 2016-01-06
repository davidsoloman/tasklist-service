package com.bjedrzejewski.tasklistservice;

import com.google.common.base.Optional;
import com.codahale.metrics.annotation.Timed;
import com.google.common.io.CharStreams;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bartoszjedrzejewski on 03/01/2016.
 */
@Path("/task-list")
@Produces(MediaType.APPLICATION_JSON)
public class TaskListResource {
    private final int maxLength;
    private final AtomicLong counter;

    public TaskListResource(int maxLength) {
        this.maxLength = maxLength;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public List<Task> listTasks(@QueryParam("contains") Optional<String> contains) {
        List<Task> tasks = new ArrayList<Task>();

        String query = contains.or("");

        try {
            Process p = Runtime.getRuntime().exec("ps -e");
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            //Dropwizard comes with google guava
            List<String> lines = CharStreams.readLines(input);
            for(int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if(line.contains(query)) {
                    tasks.add(new Task(counter.getAndIncrement(), line.substring(0, Math.min(line.length(), maxLength))));
                }
            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }


        return tasks;
    }
}