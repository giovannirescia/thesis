module Main (main) 
where

import Control.Monad ( unless )

import System.Console.CmdArgs

import System.IO           ( hPrint, stderr ) 
import System.Exit         ( exitWith, ExitCode(ExitFailure) )

import Prelude hiding ( catch )
import Control.Exception   ( catch, SomeException )

import Sy4NCL.CommandLine( defaultParams, checkParams)
import Sy4NCL.Main

main :: IO ()
main = runCmdLineVersion
        `catch` \(e::SomeException) -> do
                let msg = show e
                unless (msg == "ExitSuccess") $ hPrint stderr msg
                exit r_RUNTIME_ERROR
    where r_RUNTIME_ERROR = 13

exit :: Int -> IO a
exit = exitWith . ExitFailure

runCmdLineVersion :: IO ()
runCmdLineVersion =
 do  clp  <- cmdArgs_ $ defaultParams 
     clpOK <- checkParams clp
     if clpOK
      then do 
            runWithParams clp
      else (putStrLn . show) "Bad Arguments!!!"
            

--header :: String
--header = unlines ["qbf2ml v 0.0.0",
--                  "E. Orbe. (c) 2012.",
--                  "http://http://hub.darcs.net/ezequiel/qbf2ml"]
--
--gplTag :: [String]
--gplTag = [
--    "This program is distributed in the hope that it will be useful,",
--    "but WITHOUT ANY WARRANTY; without even the implied warranty of",
--    "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the",
--    "GNU General Public License for more details."]
