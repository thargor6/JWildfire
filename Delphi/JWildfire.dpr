program JWildfire;

uses
  Dialogs, Forms, SysUtils, ShellAPI, WinProcs, Registry, Classes;

{$R *.res}

function GetProgramFilesDir32: String;
var
  reg: TRegistry;
begin
  reg := TRegistry.Create;
  try
    reg.RootKey := HKEY_LOCAL_MACHINE;
    reg.OpenKey('SOFTWARE\Microsoft\Windows\CurrentVersion', False);
    Result := reg.ReadString('ProgramFilesDir');
  finally
    reg.Free;
  end;
end;

procedure ShowError(const PMessage: String);
begin
  ShowMessage( PMessage );
  raise Exception.Create(PMessage);
end;

procedure GetSubDirectories(const directory : string; list : TStrings);
var
  sr : TSearchRec;
begin
  try
    if FindFirst(IncludeTrailingPathDelimiter(directory) + '*.*', faDirectory, sr) < 0 then
      Exit
    else
      repeat
       if ((sr.Attr and faDirectory <> 0) AND (sr.Name <> '.') AND (sr.Name <> '..')) then
          List.Add(IncludeTrailingPathDelimiter(directory) + sr.Name) ;
      until FindNext(sr) <> 0;
  finally
    SysUtils.FindClose(sr) ;
  end;
end;

function FindJava(const PBaseDir: String) : String;
const
  FromVersion = 7;
  ToVersion = 9;
var
  Dirs: TStringList;
  SubDir, Cmd: String;
  I, J: Integer;
begin
  Result := '';
  Dirs := TStringList.Create;
  try
    GetSubDirectories(PBaseDir+'\Java\', Dirs);
    Dirs.Sort;
    for I := Dirs.Count-1 downto 0 do begin
      SubDir := ExtractFileName( Dirs[I] );
      for J:= FromVersion to ToVersion do begin
        if (Pos('jdk'+IntToStr(J), SubDir) = 1) or (Pos('jre'+IntToStr(J), SubDir) = 1) then begin
          Cmd := PBaseDir+'\Java\'+SubDir+'\bin\java.exe';
          if FileExists(Cmd) then begin
            Result := Cmd;
            exit;
          end;
        end;
      end;
    end;
  finally
    Dirs.Free;
  end;
end;

function quote(const PCmd: String): String;
begin
 if Pos(' ', PCmd) > 0 then
   Result := '"'+PCmd+'"'
 else
   Result := PCmd;
end;

function GetJavaCmd(const PWorkingDir: String): String;
const
  JavaCmd = 'java.exe';
var
  BaseDir, Cmd: String;
begin
  Result := JavaCmd;

  Cmd := PWorkingDir+'jre\bin\'+JavaCmd;
  if FileExists(Cmd) then begin
    Result := quote(Cmd);
    exit;
  end;

  BaseDir := 'C:\Program Files';
  if DirectoryExists( BaseDir ) then begin
    Cmd := FindJava( BaseDir );
    if Cmd<>'' then begin
      Result := quote(Cmd);
      exit;
    end;
  end;

  BaseDir := GetProgramFilesDir32;
  if DirectoryExists( BaseDir ) then begin
    Cmd := FindJava( BaseDir );
    if Cmd<>'' then begin
      Result := quote(Cmd);
      exit;
    end;
  end;
end;

procedure LaunchApp;
const
  JarName = 'j-wildfire-launcher.jar';
var
  WorkingDir: String;
  JarPath: String;
  JavaOptions: String;
  Cmd: String;
  DebugMode: Boolean;
begin
  DebugMode := (ParamCount > 0) and (AnsiLowerCase(ParamStr(1))='-debug');
  WorkingDir := ExtractFilePath(Application.ExeName);
  JarPath := WorkingDir+JarName;

  if Pos(' ', JarPath) > 1 then
    Cmd := GetJavaCmd( WorkingDir )+' -jar "'+JarPath+'"'
  else
    Cmd := GetJavaCmd( WorkingDir ) +' -jar '+JarPath;

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
