package com.jkudla.drawingTest.command;

import com.jkudla.drawingTest.drawing.DrawingEnum;
import com.jkudla.drawingTest.exception.WrongCommandException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandController {

    static final String MAX_OPERATION_LENGTH = "Maximum operation name length is: ";
    static final String WRONG_OPERATION = "Wrong operation name: ";
    static final String NO_OPERATION = "Couldn't find operation name: ";

    public static void sendCommand(String command) throws WrongCommandException {
        command = getTrimmedAndUpperCasedCommand(command);
        String operation = findOperation(command);
        List<String> parameters = findParameters(command);
        Optional<DrawingEnum> foundDrawing = Arrays.stream(DrawingEnum.values()).filter(d -> d.getOperation() == operation).findFirst();
        if (foundDrawing.isPresent()) {
            DrawingEnum drawing = foundDrawing.get();
            drawing.validate(parameters);
            drawing.draw(parameters);
        } else {
            throw new WrongCommandException(WRONG_OPERATION + operation);
        }
    }

    private static String getTrimmedAndUpperCasedCommand(String command) {
        return command.trim().toUpperCase();
    }

    public static String findOperation(String command) throws WrongCommandException {
        Pattern pattern = Pattern.compile(CommandConstants.COMMAND_LETTER_REGEX);
        Matcher matcher = pattern.matcher(command);

        String operation;
        if (matcher.find()) {
            operation = matcher.group(0);
            if(operation.length() > CommandConstants.MAX_OPERATION_NAME_LENGTH){
                throw new WrongCommandException(MAX_OPERATION_LENGTH + CommandConstants.MAX_OPERATION_NAME_LENGTH);
            }
            return operation;
        }
        throw new WrongCommandException(NO_OPERATION);
    }

    static List<String> findParameters(String command) {
        String[] parameterArray = command.split("\\s+");
        parameterArray = removeOperation(parameterArray);

        List<String> parameters = Arrays.asList(parameterArray);
        return parameters;
    }

    private static String[] removeOperation(String[] parameterArray) {
        return Arrays.copyOfRange(parameterArray, 1, parameterArray.length);
    }
}