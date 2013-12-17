program JWildfire;

uses
  Dialogs, Forms, SysUtils, ShellAPI, WinProcs;

{$R *.res}

procedure LaunchApp;
const
  JarName = 'j-wildfire-launcher.jar';
  JavaCmd = 'java';
var
  WorkingDir: String;
  JarPath: String;
  JavaOptions: String;
begin
  WorkingDir := ExtractFilePath(Application.ExeName);
  JarPath := WorkingDir+'\'+JarName;
  if not FileExists(JarPath) then
    raise Exception.Create('JWildfire launcher archive not found (Expected path: <'+JarPath+'>)');
  JavaOptions := '-jar '+JarPath;
  if ShellExecute(Application.Handle,
                 'open',
                 PChar(JavaCmd),
                 PChar(JavaOptions), PChar(WorkingDir), SW_SHOWMINNOACTIVE) <= 32 then
    raise Exception.Create('Error starting the JWildfire launcher (executed command: <'+JavaCmd+' '+JavaOptions+'>)');
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
