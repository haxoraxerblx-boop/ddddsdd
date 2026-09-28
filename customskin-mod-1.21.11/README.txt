NO-INSTALL BUILD (GitHub):
1. Make a free GitHub account, create a new empty repository.
2. Click "uploading an existing file", drag in EVERYTHING from this unzipped folder
   (including the hidden .github folder), commit.
3. Open the Actions tab -> wait for the build to finish (~3 min) -> click the run ->
   download "customskin-jar" under Artifacts. Unzip it; use the jar WITHOUT "-sources".
4. Put the jar in .minecraft/mods with Fabric API (Fabric loader for 1.21.11).
5. Launch, drag a 64x64 PNG into .minecraft/customskin. Put "slim" in the filename for slim arms.
