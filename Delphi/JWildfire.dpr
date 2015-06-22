program JWildfire;

uses
  Dialogs, Forms, SysUtils, ShellAPI, WinProcs;

{$R *.res}

procedure LaunchApp;
const
  JarName = 'j-wildfire-launcher.jar';
var
  WorkingDir: String;
  JarPath: String;
  JavaOptions: String;
  Cmd: String;
  DebugMode: Boolean;
  JavaCmd, LocalJava: String;

begin
  DebugMode := (ParamCount > 0) and (AnsiLowerCase(ParamStr(1))='-debug');
  WorkingDir := ExtractFilePath(Application.ExeName);
  JarPath := WorkingDir+JarName;
  JavaCmd := 'java.exe';
  LocalJava := WorkingDir+'jre\bin\'+JavaCmd;

  if FileExists(LocalJava) then begin
    if Pos(' ', LocalJava) > 0 then
      JavaCmd := '"'+LocalJava+'"'
    else
      JavaCmd := LocalJava;
  end;


  if Pos(' ', JarPath) > 1 then
    Cmd := JavaCmd+' -jar "'+JarPath+'"'
  else
    Cmd := JavaCmd+' -jar '+JarPath;

  if(DebugMode) then
    ShowMessage(Cmd);

  if not FileExists(JarPath) then
    raise Exception.Create('JWildfire launcher archive not found (Expected path: <'+JarPath+'>)');

  if ShellExecute(Application.Handle,
                 'open',
                 PChar('cmd'),
                 PChar('/C '+Cmd), PChar(WorkingDir), 0) <= 32 then
    raise Exception.Create('Error starting the JWildfire launcher (executed command: <'+Cmd+'>)');
end;


begin
  Application.Initialize;
  Application.Title := 'JWildfire launcher for Windows';
  Application.Run;
  try
    LaunchApp;
  except
    on E:Exception do begin
      ShowMessage(E.Message);
    end;
  end;
end.
