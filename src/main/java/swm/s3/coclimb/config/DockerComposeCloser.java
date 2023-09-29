package swm.s3.coclimb.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
public class DockerComposeCloser {

    public static void main(String[] args) {

        process("docker-compose down".split(" "),
                "src/test/resources/docker");
    }
    public static boolean process(String[] command, String workingDir) {
        try {
            // 명령어를 실행할 프로세스를 생성합니다.
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new File(workingDir));


            // 프로세스를 실행합니다.
            Process process = processBuilder.start();

            // 프로세스의 출력을 읽어옵니다.
            readProcessOutput(process);

            // 프로세스가 완료될 때까지 기다립니다.
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("완");
                return true;
            } else {
                System.out.println("에러");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void readProcessOutput(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("line = " + line);
        }
    }

}
